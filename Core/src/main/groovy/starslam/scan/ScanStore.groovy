package starslam.scan

import starslam.IDbConnection
import starslam.OpenDatabase

final class ScanStore implements IScanStore {
	final IDbConnection dbConnector
	
	final def scanSelect = "id, project_id, created, completed, production_date, root_path, processing_time, status, file_glob"
	final def scanRowMapper = { it ->
		return new ScanInfo([
			id:it.id
			, projectId:it.project_id
			, initiatedTime:new Date(it.created)
			, completionTime:(it.completed == null)?null:new Date(it.completed)
			, productionDate:(it.production_date == null)?null:new Date(it.production_date)
			, rootPath:it.root_path
			, processingTime:it.processing_time
			, status:ScanStatus.values()[it.status]
			, fileGlob:it.file_glob
		])
	}
	
	final def scannedFileRowMapper = { it ->
		return new ScannedFile([
			id:it.id
			, scanId:it.scan_id
			, filename:it.filename
			, relativePath:it.relative_path
			, fullPath:it.fullpath
			, isNew:it.is_new
			, hasChanged:it.has_changed
			, data:it.data.asciiStream.text
			, scannerPlugin:it.scanner
			, md5:it.md5
		])
	}
	
	public ScanStore(IDbConnection conn) {
		dbConnector = conn
	}

	@Override
	public String persist(ScanInfo scan) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def id = scan.id?:UUID.randomUUID().toString()
				sql.execute("""
					merge into scan(
						id
						, project_id
						, created
						, completed
						, production_date
						, root_path
						, processing_time
						, status
						, file_glob
					)
					key (id)
					values (
						${id}
						, ${scan.projectId}
						, ${scan.initiatedTime.time}
						, ${scan.completionTime?.time}
						, ${scan.productionDate?.time}
						, ${scan.rootPath}
						, ${scan.processingTime}
						, ${scan.status.ordinal()}
						, ${scan.fileGlob}
					)
				""")
				return id
			}
		}
	}

	@Override
	public ScanInfo retrieveScan(String id) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def row = sql.firstRow("select "+scanSelect+" from scan where id = '${id}'")
				return (row == null) ? null : scanRowMapper(row)
			}
		}
	}

	@Override
	public ScanInfo retrieveLatestScanForProject(String projectId) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def row = sql.firstRow("select "+scanSelect+" from scan where project_id = '${projectId}' order by created desc ")
				return (row == null) ? null : scanRowMapper(row)
			}
		}
	}

	@Override
	public Iterable<ScanInfo> scansForProject(String projectId) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def rtn = []
				sql.eachRow("select "+scanSelect+" from scan where project_id = '${projectId}' order by created desc") { row ->
					rtn << scanRowMapper(row)
				}
				return rtn
			}
		}
	}

	@Override
	public String persist(ScannedFile file) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def id = UUID.randomUUID().toString()
				sql.execute("""
					insert into scannedFile (
						id
						, scan_id
						, filename
						, relative_path
						, fullpath
						, is_new
						, has_changed
						, data
						, scanner
						, md5
					) values (
						${id}
						, ${file.scanId}
						, ${file.filename}
						, ${file.relativePath}
						, ${file.fullPath}
						, ${file.isNew}
						, ${file.hasChanged}
						, ${file.data}
						, ${file.scannerPlugin}
						, ${file.md5}
					)
				""")
				return id
			}
		}
	}

	@Override
	public ScannedFile retrieveLatestScannedFileWithRelativePath(String projectId, String path) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def row = sql.firstRow("""
					select sf.id, scan_id, filename, relative_path, fullpath, is_new, has_changed, data, scanner, md5
					from scannedFile sf
						inner join scan s on sf.scan_id = s.id
					where s.project_id = ${projectId}
					and sf.relative_path = ${path}
					order by s.CREATED desc
				""")
				return (row == null) ? null : scannedFileRowMapper(row)
			}
		}
	}

	@Override
	public Iterable<ScannedFile> filesForScan(String scanId) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def rtn = []
				sql.eachRow("""
					select sf.id, scan_id, filename, relative_path, fullpath, is_new, has_changed, data, scanner, md5
					from scannedFile sf
					where sf.scan_id = ${scanId}
					order by sf.relative_path
				""") { row ->
					rtn << scannedFileRowMapper(row)
				}
				return rtn
			}
		}
	}

	@Override
	public int filesForScanCount(String scanId) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				return sql.firstRow("""
					select count(*) cnt
					from scannedFile sf
					where sf.scan_id = ${scanId}
				""").cnt
			}
		}
	}

}
