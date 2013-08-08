package starslam.scan.plugins

import groovy.transform.Immutable
import java.io.File;

@Immutable
class ExternalPlugin implements IPlugin {
	String name
	String executable

	@Override
	public PluginResponse process(File file) {
		def process = "${executable} ${file.canonicalPath}".execute()
		def output = process.text
		return new PluginResponse(output)
	}

}
