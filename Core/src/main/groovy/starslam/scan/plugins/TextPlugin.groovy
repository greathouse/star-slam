package starslam.scan.plugins

import java.io.File;

class TextPlugin implements IPlugin {

	@Override
	public String getName() {
		return "internal-txt.v1";
	}

	@Override
	public PluginResponse process(File file) {
		return null;
	}

}
