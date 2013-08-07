package starslam.scan.plugins;

import java.io.File;

public interface IPlugin {
	String getName();
	PluginResponse process(File file);
}
