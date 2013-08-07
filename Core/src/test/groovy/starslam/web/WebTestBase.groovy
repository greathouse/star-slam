package starslam.web

import static org.ratpackframework.guice.Guice.handler
import static org.ratpackframework.handling.Handlers.chain

import org.ratpackframework.server.RatpackServer
import org.ratpackframework.server.RatpackServerBuilder

import starslam.Handler
import starslam.ModuleBootstrap
import starslam.StarSlamLaunchConfig
import starslam.TestBase

abstract class WebTestBase extends TestBase {
	private static RatpackServer ratpackServer
	
	@Override
	protected void onPostSetup() {
		if (ratpackServer == null) {
			ratpackServer = RatpackServerBuilder.build(new StarSlamLaunchConfig(DBURL))
		}
		
		if (false == ratpackServer.isRunning()) {
			ratpackServer.start()
		}
	}
}
