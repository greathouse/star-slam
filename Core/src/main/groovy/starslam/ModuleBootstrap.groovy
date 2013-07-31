package starslam

import org.ratpackframework.guice.ModuleRegistry
import org.ratpackframework.util.Action

import starslam.scan.ScanModule

class ModuleBootstrap implements Action<ModuleRegistry> {
	String dbUrl
	
	public ModuleBootstrap(String dbUrl) {
		this.dbUrl = dbUrl
	}
	
	@Override
	public void execute(ModuleRegistry moduleRegistry) {
		moduleRegistry.register(new ScanModule(dbUrl))
	}

}
