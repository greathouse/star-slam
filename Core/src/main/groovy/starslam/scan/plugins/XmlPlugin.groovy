package starslam.scan.plugins

import java.io.File;
import groovy.transform.Immutable

@Immutable
class XmlPlugin implements IPlugin {

	@Override
	public String getName() {
		return "internal-xml.v1";
	}

	@Override
	public PluginResponse process(File file) {
		return null;
	}

}
