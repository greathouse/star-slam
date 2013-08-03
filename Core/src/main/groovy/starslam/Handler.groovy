package starslam

import static org.ratpackframework.guice.Guice.handler
import static org.ratpackframework.handling.Handlers.*

import org.ratpackframework.handling.Chain
import org.ratpackframework.util.Action

import starslam.web.assets.AssetHandler
import starslam.web.project.ProjectApi

class Handler implements Action<Chain> {
	public Handler(){
		this(System.properties['user.dir'])
	}
	
	public Handler(String templatePath) {
	}
	
	@Override
	public void execute(Chain handlers) {
		handlers.add(handler(ProjectApi))
				
		handlers.add(handler(AssetHandler))
//		handlers.add(
//			ClosureHandlers.get {
//				new DefaultTemplateRenderer(new File(TEMPLATE_PATH),it, RENDERING_ENGINE).render "index.html", title: "Index"
//			}
//		)
//		
//		handlers.add (
//			ClosureHandlers.get('blog') {
//				new DefaultTemplateRenderer(new File(TEMPLATE_PATH),it, RENDERING_ENGINE).render "blog.html", title: "Index"
//			}
//		)
//		
//		handlers.add(
//			ClosureHandlers.get('scans') {
//				handler(ScanListHandler.class)
//			}
//		)
//		
//		handlers.add ( 
//			Handlers.assets(/C:\Users\kofspades\projects\star-slam\Core\src\ratpack\public/, [] as String[])
//		)
	}
}
