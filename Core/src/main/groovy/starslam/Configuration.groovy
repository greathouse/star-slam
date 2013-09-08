package starslam

import com.google.common.io.Files
import groovy.sql.Sql
import org.springframework.context.annotation.Bean

@org.springframework.context.annotation.Configuration
class Configuration {
	public static final String DBURL = "jdbc:h2:~/star-slam/prod"
	@Bean
	public String dbUrl() {
		DBURL
	}

	@Bean
	public File pluginDirectory() {
		Files.createTempDir()
	}

	@Bean String pluginDirectoryPath() {
		pluginDirectory().canonicalPath
	}

	@Bean
	public IDbConnection dbConnection() {
		new DatabaseConnector(dbUrl())
	}
}
