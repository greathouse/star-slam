package starslam

import static org.ratpackframework.guice.Guice.handler
import static org.ratpackframework.handling.Handlers.*

import org.ratpackframework.groovy.templating.internal.DefaultTemplatingConfig
import org.ratpackframework.groovy.templating.internal.GroovyTemplateRenderingEngine
import org.ratpackframework.handling.Chain
import org.ratpackframework.util.Action

import starslam.web.ScanListHandler
import starslam.web.assets.AssetHandler

class Handler implements Action<Chain> {
	
	final String TEMPLATE_PATH
	final def RENDERING_ENGINE
	
	public Handler(){
		this(System.properties['user.dir'])
	}
	
	public Handler(String templatePath) {
		TEMPLATE_PATH = templatePath
		RENDERING_ENGINE = new GroovyTemplateRenderingEngine(new DefaultTemplatingConfig(TEMPLATE_PATH, 500, true))
	}
	
	@Override
	public void execute(Chain handlers) {
		handlers.add(get("scans", handler(ScanListHandler.class)))
		
		handlers.add(handler(AssetHandler.class))
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
