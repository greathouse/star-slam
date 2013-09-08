package starslam

import com.google.common.io.Files
import groovy.sql.Sql
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfiguration {
	@Bean
	public String dbUrl() {
		'jdbc:h2:~/star-slam/star-slam-test'
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
		def conn = {
			Sql.newInstance(dbUrl(), '', '', 'org.h2.Driver')
		} as IDbConnection

		conn
	}
}
