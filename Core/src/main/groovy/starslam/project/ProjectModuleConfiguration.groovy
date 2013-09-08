package starslam.project

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import starslam.IDbConnection

@Configuration
class ProjectModuleConfiguration {
	@Autowired IDbConnection dbConnection

	@Bean
	public IProjectStore projectStore() {
		return new ProjectService(dbConnection)
	}
}
