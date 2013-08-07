package starslam

import static org.ratpackframework.guice.Guice.handler
import static org.ratpackframework.handling.Handlers.chain

import org.ratpackframework.server.RatpackServerBuilder

class Bootstrapper {
	Bootstrapper porpoise(String dbUrl) {
		ClassLoader parent = getClass().getClassLoader();
		GroovyClassLoader loader = new GroovyClassLoader(parent);
		Class groovyClass = loader.parseClass(parent.getResourceAsStream("porpoise/Porpoise.groovy"),"porpoise/Porpoise.groovy");
		
		// let's call some method on an instance
		GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
		groovyObject.invokeMethod("main", ['-SF', '-d','src/main/resources/sql', '-U',dbUrl, '--no-exit'] as String[]);
		
		this
	}
	
	Bootstrapper ratpack(String dbUrl) {
		def ratpackServer = RatpackServerBuilder.build(new StarSlamLaunchConfig(dbUrl))
		ratpackServer.start()
		
		this
	}
	
}
