package starslam
import groovy.sql.Sql
import com.google.common.io.Files
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.junit.Before
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import starslam.project.ProjectModuleConfiguration
import starslam.scan.ScanModuleConfiguration
import starslam.scan.plugins.PluginModuleConfiguration
import starslam.web.WebConfig

abstract class TestBase {
	protected Sql sql

	protected void onPreSetup() {}
	protected void onPostSetup() {}
	
	private static porpoised = false;
	private static wired = false;
	protected static File pluginDirectory
	protected static ApplicationContext context

	private Random random = new Random()

	@Before
	public final void setUp() {
		onPreSetup()

		if (!wired) {
			context = new AnnotationConfigWebApplicationContext()
			context.register(
				TestConfiguration
				,	ProjectModuleConfiguration
				, ScanModuleConfiguration
				, PluginModuleConfiguration
				, WebConfig
			)

			startJetty(context)

			pluginDirectory = context.getBean("pluginDirectory")
			wired = true
		}

		if (!porpoised) { new Bootstrapper().porpoise(context.getBean("dbUrl")) ; porpoised = true }
		sql = context.getBean(DbConnection).connection
		cleanUpDatabase()

		onPostSetup()
	}

	protected int randomInt(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}

	private static void startJetty(def applicationContext) {
		final def servletHolder = new ServletHolder(new DispatcherServlet(applicationContext))
		final def context = new ServletContextHandler()
		context.setContextPath("/")
		context.addServlet(servletHolder, "/*")
		final def server = new Server(Integer.valueOf("9090"))
		server.setHandler(context)
		server.start()
	}
	
	private void cleanUpDatabase() {
		[
			'ScannedFile'
			,'Scan'
			, 'Project'
		].each {
			sql.execute("delete from ${it}".toString())
		}
	}
	
	protected File rootPath() {
		return Files.createTempDir()
	}
	
	protected File createFile(def path, def subdir, def suffix) {
		def subdirPath = new File(path, subdir)
		subdirPath.mkdirs()
		return createFile(subdirPath, suffix)
	}
	
	protected File createFile(def path, def suffix) {
		def textFile = new File(path, UUID.randomUUID().toString()+"${suffix}")
		Files.touch(textFile)
		return textFile
	}
	
}