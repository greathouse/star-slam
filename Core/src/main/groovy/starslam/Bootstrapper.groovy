package starslam

import static org.ratpackframework.guice.Guice.handler
import static org.ratpackframework.handling.Handlers.chain

import org.ratpackframework.bootstrap.RatpackServer
import org.ratpackframework.bootstrap.RatpackServerBuilder

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
		def modulesConfigurer = new ModuleBootstrap(dbUrl);
		
		def myHandler = chain(new Handler(/C:\Users\kofspades\projects\star-slam\Core\src\ratpack\templates/))
		def guiceHandler = handler(modulesConfigurer, myHandler);
		RatpackServerBuilder ratpackServerBuilder = new RatpackServerBuilder(guiceHandler, new File(System.getProperty("user.dir")));

		// Start the server and block
		RatpackServer ratpackServer = ratpackServerBuilder.build();
		ratpackServer.start();
		
		this
	}
	
}
