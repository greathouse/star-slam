package starslam

import static org.ratpackframework.handling.Handlers.*

import org.ratpackframework.guice.ModuleRegistry;
import org.ratpackframework.handling.Chain
import org.ratpackframework.util.Action

import starslam.web.assets.AssetHandler
import starslam.web.project.ProjectApi
import starslam.web.scan.ScanApi

class Handler implements Action<Chain> {
	
	@Override
	public void execute(Chain chain) {
		chain.add(path("projects/:projectId/scans/:id", chain.registry.get(ScanApi)))
		chain.add(path("projects/:projectId/scans",chain.registry.get(ScanApi)))
		
		chain.add(path("projects/:id", chain.registry.get(ProjectApi)))
		chain.add(path("projects",chain.registry.get(ProjectApi)))
				
		chain.add(chain.registry.get(AssetHandler))
	}
}
