package starslam

import groovy.sql.Sql

class Main {

	static final String DB_URL = "jdbc:h2:tcp://localhost/~/star-slam"
	static void main(String[] args) {
		new Bootstrapper().porpoise(DB_URL)
		def arguments = new Arguments()
		arguments.with {
			projectRoot = /C:\Users\kofspades\projects\star-slam\StarSlamSampleProject/
			projectName = 'Star Slam Sample Project'
			configFilePattern = "*.xml"
			sqlFileDirectory = "SQL.Migration"
		}
//		new Main(arguments, DB_URL).scan()
	}
	
	Main(Arguments args, String dburl) {
		Session.args = args
		Session.sql = Sql.newInstance(dburl, '', '', 'org.h2.Driver')
	}
}
