package starslam.scan.plugins

import groovy.io.FileType
import groovy.json.JsonSlurper

import com.google.common.io.Files

class PluginService implements IPluginService {
	final private String PLUGIN_PATH
	private Map<String, IPlugin> registeredPlugins
	
	public PluginService(String pluginPath) {
		PLUGIN_PATH = pluginPath
	}
	
	private Map<String, IPlugin> loadPlugins() {
		def slurper = new JsonSlurper()
		def rtn = [:]
		new File(PLUGIN_PATH).eachFileRecurse(FileType.FILES) { file ->
			if (file.name == 'plugin.json') {
				def json = slurper.parse(new FileReader(file))
				rtn.put(json.filetype, new ExternalPlugin([
					name:json.name
					, executable:file.parentFile.canonicalPath+"/"+json.executable
				]))
			}
		}
		return rtn
	}

	@Override
	public IPlugin get(File file) {
		registeredPlugins = Collections.unmodifiableMap(loadPlugins())

		def extension = Files.getFileExtension(file.canonicalPath)
		switch(extension) {
			case "txt": return new TextPlugin()
			case "xml": return new XmlPlugin()
			default:
				return registeredPlugins[extension]
		}
		return null;
	}

}
