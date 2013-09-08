package starslam.project

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import starslam.IDbConnection

class ProjectModuleConfiguration {
	@Autowired IDbConnection dbConnection

	@Bean
	public IProjectStore projectStore() {
		return new ProjectService(dbConnection)
	}
}
