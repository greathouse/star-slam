package starslam
import groovy.sql.Sql
import junit.framework.TestCase

import com.google.common.io.Files
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import starslam.project.ProjectModuleConfiguration
import starslam.scan.ScanModuleConfiguration
import starslam.scan.plugins.PluginModuleConfiguration

abstract class TestBase extends TestCase {
	protected Sql sql

	protected void onPreSetup() {}
	protected void onPostSetup() {}
	
	private static porpoised = false;
	private static wired = false;
	protected static File pluginDirectory
	protected static ApplicationContext context
	
	public final void setUp() {
		onPreSetup()

		if (!wired) {
			context = new AnnotationConfigApplicationContext(
							TestConfiguration
							,	ProjectModuleConfiguration
							, ScanModuleConfiguration
							, PluginModuleConfiguration
			)
			pluginDirectory = context.getBean("pluginDirectory")
			wired = true
		}

		if (!porpoised) { new Bootstrapper().porpoise(context.getBean("dbUrl")) ; porpoised = true }
		sql = context.getBean(IDbConnection).connection
		cleanUpDatabase()

		onPostSetup()
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