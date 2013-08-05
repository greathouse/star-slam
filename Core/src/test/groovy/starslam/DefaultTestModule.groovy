package starslam

import starslam.project.IProjectStore
import starslam.project.ProjectService
import starslam.scan.IScanService
import starslam.scan.IScanStore
import starslam.scan.ScanService
import starslam.scan.ScanStore

import com.google.inject.AbstractModule
import com.google.inject.name.Names

class DefaultTestModule extends AbstractModule {
	final String DBURL
	
	public DefaultTestModule(String dbUrl) {
		DBURL = dbUrl 
	}
	
	@Override
	protected void configure() {
		bind(String).annotatedWith(Names.named("JDBC_URL")).toInstance(DBURL)
		bind(IDbConnection).to(DatabaseConnector)
	}

}
