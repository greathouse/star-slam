package starslam

import starslam.project.IProjectStore
import starslam.project.ProjectService
import starslam.scan.IScanService
import starslam.scan.ScanService

import com.google.inject.AbstractModule
import com.google.inject.name.Names

class DefaultTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(String).annotatedWith(Names.named("JDBC_URL")).toInstance('jdbc:h2:~/star-slam-test')
		bind(IDbConnection).to(DatabaseConnector)
		bind(IProjectStore).to(ProjectService)
		bind(IScanService).to(ScanService)
	}

}
