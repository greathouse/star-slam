@GrabConfig(systemClassLoader=true)
@Grapes([
	@Grab(group='com.h2database', module='h2', version='1.3.170')
])

import groovy.sql.*

dbUrl = "jdbc:h2:tcp://localhost/~/star-slam"
sql = Sql.newInstance(dbUrl, '', '', 'org.h2.Driver')

arguments = new Arguments()
arguments.projectRoot = /C:\Users\kofspades\projects\star-slam\StarSlamSampleProject/
arguments.projectName = 'Star Slam Sample Project'
arguments.configFilePattern = "*.xml"
arguments.sqlFileDirectory = "SQL.Migration"

init()
processProject()


def init() {
	run(new File('porpoise/Porpoise.groovy'), ['-SF', '-d','sql', '-U',dbUrl, '--no-exit'] as String[])
}

def processProject() {
	def project = persistProject();
	assert project.id != null
	
	persistScan(project)
	scanConfigFiles()
}

def persistProject() {
	def rtn = [:]
	def row = sql.firstRow("select * from project where name = ${arguments.projectName}")
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
		sql.execute("insert into project (id, name, created) values (${newId}, ${arguments.projectName}, ${time})")
		rtn.id = newId
		rtn.name = arguments.projectName
		rtn.created = time
	}
	return rtn
}

def persistScan(project) {
	sql.execute("""
		insert into scan (id
			, project_id
			, directory
			, created
			, CONFIG_FILE_PATTERN
			, sql_file_directory) 
		values (
			${UUID.randomUUID().toString()}
			, ${project.id}
			, ${arguments.projectRoot}
			, ${new Date().time}
			, ${arguments.configFilePattern}
			, ${arguments.sqlFileDirectory}
		)
	""")
	
}

def scanConfigFiles() {
	def root = new File(arguments.projectRoot)
	root.
}

class Arguments {
	def projectRoot
	def projectName
	def configFilePattern
	def sqlFileDirectory
}