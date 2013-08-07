package starslam.scan.plugins

import com.google.inject.Inject
import com.google.inject.name.Named

class PluginService implements IPluginService {
	final String PLUGIN_PATH
	
	@Inject
	public PluginService(@Named("plugin.path") String pluginPath) {
		PLUGIN_PATH = pluginPath
	}

	@Override
	public IPlugin get(File file) {
		FilenameUtils.
		return new TextPlugin();
	}

}
