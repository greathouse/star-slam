package starslam

import groovy.sql.Sql
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.*
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.FileVisitResult
import java.nio.file.SimpleFileVisitor
import java.security.MessageDigest

class ScanService {

	void scan(RunScanArguments args) {
		def project = persistProject(args.projectName)
		def scan = persistScan(args, project)
		scanConfigs(scan)
	}

	def latestScan(String projectName) {
		def projectRow = Session.sql.firstRow("select * from project where name = ${projectName}")
		def scanRow = Session.sql.firstRow("select * from scan where project_id = ${projectRow.id} order by created desc")
		def rtn = [:]
		rtn.with {
			name = projectRow.name
			created = projectRow.created
			projectRoot = scanRow.directory
			configFilePattern = scanRow.config_file_pattern
			sqlFileDirectory = scanRow.sql_file_directory
			deployTime = scanRow.deploy_time
			configFiles = []
		}
		
		Session.sql.eachRow("select * from configfile where scan_id = ${scanRow.id}") {
			rtn.configFiles.add([
				scanId : it.scan_id
				, created : it.created
				, name : it.name
				, md5 : it.md5
				, isNew : it.is_new
				, hasChanged : it.has_changed
			])
		}

		rtn
	}

	private void scanConfigs(scan) {
		def matcher = FileSystems.getDefault().getPathMatcher("glob:"+scan.configFilePattern);
		Files.walkFileTree(Paths.get(scan.directory), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
				def fileName = filePath.fileName
				if (matcher.matches(fileName)) {
					def md5 = generateMd5(new File(filePath.toString()))
					Session.sql.execute("""
				insert into ConfigFile (
					id
					, scan_id
					, created
					, name	
					, md5
					, is_new
					, has_changed
				)
				values (
					${UUID.randomUUID().toString()}
					, ${scan.id}
					, ${new Date().time}
					, ${filePath.toString()}
					, ${md5}
					, ${true}
					, ${false}
				)
				""")
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private def persistProject(String projectName) {
		def rtn = [:]
		def row = Session.sql.firstRow("select * from project where name = ${projectName}")
		if (row) {
			rtn.with {
				id = row.id
				name = row.name
				created = row.created
			}
		}
		else {
			def newId = UUID.randomUUID().toString()
			def time = new Date().time
			Session.sql.execute("insert into project (id, name, created) values (${newId}, ${projectName}, ${time})")
			rtn.id = newId
			rtn.name = projectName
			rtn.created = time
		}
		return rtn
	}

	private def persistScan(RunScanArguments args, project) {
		def id = UUID.randomUUID().toString()
		def date = new Date().time
		def deployTime = new Date().time
		Session.sql.execute("""
			insert into scan (
				id
				, project_id
				, directory
				, created
				, CONFIG_FILE_PATTERN
				, sql_file_directory
				, deploy_time
			) 
			values (
				${id}
				, ${project.id}
				, ${args.projectRoot}
				, ${date}
				, ${args.configFilePattern}
				, ${args.sqlFileDirectory}
				, ${deployTime}
			)
		""")
		return [
			id : id
			, projectId : project.id
			, created : date
			, directory : args.projectRoot
			, configFilePattern : args.configFilePattern
			, sqlFileDirectory : args.sqlFileDirectory
			, deployTime : deployTime
		]
	}

	private def generateMd5(final file) {
		def digest = MessageDigest.getInstance("MD5")
		file.withInputStream(){is->
			byte[] buffer = new byte[8192]
			int read = 0
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
		}
		byte[] md5sum = digest.digest()
		BigInteger bigInt = new BigInteger(1, md5sum)
		return bigInt.toString(16)
	}
}
