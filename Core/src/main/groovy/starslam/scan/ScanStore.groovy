package starslam.scan

import starslam.IDbConnection
import starslam.OpenDatabase

import com.google.inject.Inject

final class ScanStore implements IScanStore {
	final IDbConnection dbConnector
	
	final def scanRowMapper = { it ->
		return new ScanInfo([
			id:it.id
			, projectId:it.project_id
			, initiatedTime:new Date(it.created)
			, completionTime:new Date(it.completed)
			, productionDate:new Date(it.production_date)
			, rootPath:it.root_path
			, processingTime:it.processing_time
			, status:ScanStatus.values()[it.status]
		])
	}
	
	@Inject
	public ScanStore(IDbConnection conn) {
		dbConnector = conn
	}

	@Override
	public String persist(ScanInfo scan) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def id = UUID.randomUUID().toString()
				sql.execute("""
					insert into scan(
						id
						, project_id
						, created
						, completed
						, production_date
						, root_path
						, processing_time
						, status
					)
					values (
						${id}
						, ${scan.projectId}
						, ${scan.initiatedTime.time}
						, ${scan.completionTime.time}
						, ${scan.productionDate.time}
						, ${scan.rootPath}
						, ${scan.processingTime}
						, ${scan.status.ordinal()}
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
				def row = sql.firstRow("select id, project_id, created, completed, production_date, root_path, processing_time, status from scan where id = ${id}")
				return (row == null) ? null : scanRowMapper(row)
			}
		}
	}

	@Override
	public ScanInfo retrieveLatestScanForProject(String projectId) {
		return null;
	}

	@Override
	public Iterable<ScanInfo> scansForProject(String projectId) {
		return null;
	}

	@Override
	public String persist(ScannedFile file) {
		return null;
	}

	@Override
	public ScannedFile retrieveLatestScannedFileWithRelativePath(
			String projectId, String path) {
		return null;
	}

	@Override
	public Iterable<ScannedFile> filesForScan(String scanId) {
		return null;
	}

}
