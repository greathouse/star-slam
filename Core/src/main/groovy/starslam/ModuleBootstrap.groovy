package starslam

import org.ratpackframework.guice.ModuleRegistry
import org.ratpackframework.util.Action

import starslam.project.ProjectModule
import starslam.scan.ScanModule
import starslam.scan.plugins.PluginModule

class ModuleBootstrap implements Action<ModuleRegistry> {
	String dbUrl
	
	public ModuleBootstrap(String dbUrl) {
		this.dbUrl = dbUrl
	}
	
	@Override
	public void execute(ModuleRegistry moduleRegistry) {
		moduleRegistry.register(new DefaultModule(dbUrl))
		moduleRegistry.register(new ScanModule())
		moduleRegistry.register(new ProjectModule())
		
		def path = this.class.getProtectionDomain().getCodeSource().getLocation().getPath()
		def decodedPath = URLDecoder.decode(path, "UTF-8")
		moduleRegistry.register(new PluginModule(decodedPath))
	}

}
