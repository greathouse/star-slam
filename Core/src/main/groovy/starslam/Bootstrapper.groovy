package starslam

import com.google.common.io.Files
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import starslam.project.ProjectModuleConfiguration
import starslam.scan.ScanModuleConfiguration
import starslam.scan.plugins.PluginModuleConfiguration
import starslam.web.WebConfig

import java.util.regex.Pattern

class Bootstrapper {
	Bootstrapper porpoise(String dbUrl) {
		def parent = getClass().getClassLoader()
		def loader = new GroovyClassLoader(parent)
		def groovyClass = loader.parseClass(parent.getResourceAsStream("porpoise/Porpoise.groovy").text)
		
		def tempSqlDir = Files.createTempDir()
		def sqlfiles = new Reflections("sql", new ResourcesScanner()).getResources(Pattern.compile(".*\\.sql"))
		sqlfiles.each { f ->
			def outfile = new File(tempSqlDir, f.replaceAll('sql/', ''))
			def infile = ClassLoader.getSystemResourceAsStream(f)
			outfile.withWriter { w ->
				w << infile
			}
		}

		def groovyObject = (GroovyObject) groovyClass.newInstance()
		groovyObject.invokeMethod("main", ['-SF', '-d',tempSqlDir.canonicalPath, '-U',dbUrl, '--no-exit'] as String[])
		
		this
	}

	Bootstrapper jetty()  {
		final def applicationContext = new AnnotationConfigWebApplicationContext()
		applicationContext.register(
						Configuration
						,	ProjectModuleConfiguration
						, ScanModuleConfiguration
						, PluginModuleConfiguration
						, WebConfig
		)

		final def servletHolder = new ServletHolder(new DispatcherServlet(applicationContext))
		final def context = new ServletContextHandler()
		context.setContextPath("/")
		context.addServlet(servletHolder, "/*")

		def webPort = System.getenv("PORT")
		if (webPort == null || webPort.isEmpty()) {
				webPort = "9069"
		}

		final def server = new Server(Integer.valueOf(webPort))

		server.setHandler(context)

		server.start()
		server.join()

		this
	}
}
