package starslam.web

import static org.ratpackframework.guice.Guice.handler
import static org.ratpackframework.handling.Handlers.chain

import org.ratpackframework.server.DefaultRatpackServerSettings
import org.ratpackframework.server.RatpackServer
import org.ratpackframework.server.RatpackServerBuilder

import starslam.Handler
import starslam.ModuleBootstrap
import starslam.TestBase

abstract class WebTestBase extends TestBase {
	private static RatpackServer ratpackServer
	
	@Override
	protected void onPostSetup() {
		if (ratpackServer == null) {
			def modulesConfigurer = new ModuleBootstrap(DBURL)
			
			def myHandler = chain(new Handler(/C:\Users\kofspades\projects\star-slam\Core\src\ratpack\templates/))
			def settings = new DefaultRatpackServerSettings(new File(System.getProperty("user.dir")), false)
			def guiceHandler = handler(settings, modulesConfigurer, myHandler)
			def ratpackServerBuilder = new RatpackServerBuilder(settings, guiceHandler)
			
			ratpackServer = new RatpackServerBuilder(settings, guiceHandler).build()
		}
		
		if (false == ratpackServer.isRunning()) {
			ratpackServer.start()
		}
	}
}
