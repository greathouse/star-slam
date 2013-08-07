package starslam
import groovy.sql.Sql
import junit.framework.TestCase
import starslam.project.ProjectModule
import starslam.scan.ScanModule
import starslam.scan.plugins.PluginModule

import com.google.common.io.Files
import com.google.inject.Guice
import com.google.inject.Injector

abstract class TestBase extends TestCase {
	final protected def DBURL = 'jdbc:h2:~/star-slam/star-slam-test'
	protected Sql sql
	final protected IDbConnection conn = {
		Sql.newInstance(DBURL, '', '', 'org.h2.Driver')
	} as IDbConnection
	
	protected void onPreSetup() {}
	protected void onPostSetup() {}
	
	private static porpoised = false;
	private static wired = false;
	protected static Injector injector
	protected static File pluginDirectory
	
	public final void setUp() {
		onPreSetup()
		if (!porpoised) { new Bootstrapper().porpoise(DBURL) ; porpoised = true }
		sql = conn.getConnection()
		cleanUpDatabase()
		if (!wired) {
			pluginDirectory = Files.createTempDir()
			injector = Guice.createInjector(
				new DefaultModule(DBURL)
				, new ProjectModule()
				, new ScanModule()
				, new PluginModule(pluginDirectory.canonicalPath)
			)
			wired = true
		}
		
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
}