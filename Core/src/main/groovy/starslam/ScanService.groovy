package starslam

import groovy.sql.Sql

class ScanService {
	void scan() {
		def project = persistProject()
		persistScan(project)
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
		}
		
		
		rtn
	}

	private def persistProject(String projectName) {
		def rtn = [:]
		def row = Session.sql.firstRow("select * from project where name = ${Session.args.projectName}")
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
			Session.sql.execute("insert into project (id, name, created) values (${newId}, ${Session.args.projectName}, ${time})")
			rtn.id = newId
			rtn.name = Session.args.projectName
			rtn.created = time
		}
		return rtn
	}

	private def persistScan(project) {
		Session.sql.execute("""
			insert into scan (id
				, project_id
				, directory
				, created
				, CONFIG_FILE_PATTERN
				, sql_file_directory) 
			values (
				${UUID.randomUUID().toString()}
				, ${project.id}
				, ${Session.args.projectRoot}
				, ${new Date().time}
				, ${Session.args.configFilePattern}
				, ${Session.args.sqlFileDirectory}
			)
		""")
	}
}
