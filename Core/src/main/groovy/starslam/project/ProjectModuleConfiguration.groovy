package starslam.project

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import starslam.DbConnection

@Configuration
class ProjectModuleConfiguration {
	@Autowired DbConnection dbConnection

	@Bean
	public ProjectStore projectStore() {
		return new ProjectServiceDefault(dbConnection)
	}
}
