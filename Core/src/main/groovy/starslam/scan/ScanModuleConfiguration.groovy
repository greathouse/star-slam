package starslam.scan

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import starslam.IDbConnection
import starslam.project.IProjectStore

@Configuration
class ScanModuleConfiguration {
	@Autowired IDbConnection dbConnection
	@Autowired IProjectStore projectStore

	@Bean
	public IScanStore scanStore() {
		return new ScanStore(dbConnection)
	}

	@Bean
	public IScanService scanService() {
		return new ScanService(dbConnection, projectStore, scanStore())
	}
}
