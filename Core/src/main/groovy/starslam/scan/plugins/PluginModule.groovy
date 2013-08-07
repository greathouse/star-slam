package starslam.scan.plugins

import com.google.inject.AbstractModule
import com.google.inject.name.Names

class PluginModule extends AbstractModule {
	final String PLUGIN_PATH
	
	public PluginModule(String pluginPath) {
		PLUGIN_PATH = pluginPath
	}

	@Override
	protected void configure() {
		bind(String).annotatedWith(Names.named("plugin.path")).toInstance(PLUGIN_PATH)
		bind(IPluginService).to(PluginService)
	}

}
