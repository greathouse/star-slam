package starslam.scan.plugins

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PluginModuleConfiguration {
	@Autowired String pluginDirectoryPath

	@Bean
	public IPluginService pluginService() {
		return new PluginService(pluginDirectoryPath)
	}
}
