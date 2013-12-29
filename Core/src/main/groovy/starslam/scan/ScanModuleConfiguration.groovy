package starslam.scan

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import starslam.DbConnection
import starslam.project.ProjectStore

@Configuration
class ScanModuleConfiguration {
	@Autowired DbConnection dbConnection
	@Autowired ProjectStore projectStore

	@Bean
	public ScanStore scanStore() {
		return new ScanStoreDefault(dbConnection)
	}

	@Bean
	public ScanService scanService() {
		return new ScanServiceDefault(dbConnection, projectStore, scanStore())
	}
}
