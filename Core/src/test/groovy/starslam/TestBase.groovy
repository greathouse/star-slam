package starslam
import groovy.sql.Sql
import junit.framework.TestCase
import starslam.project.ProjectModule
import starslam.scan.ScanModule

import com.google.inject.Guice
import com.google.inject.Injector

abstract class TestBase extends TestCase {
	protected final def DBURL = 'jdbc:h2:~/star-slam/star-slam-test'
	protected Sql sql
	protected IDbConnection conn = {
		Sql.newInstance(DBURL, '', '', 'org.h2.Driver')
	} as IDbConnection
	protected Injector injector


	protected void onPreSetup() {}
	protected void onPostSetup() {}
	
	private static porpoised = false;
	
	public final void setUp() {
		onPreSetup()
		if (!porpoised) { new Bootstrapper().porpoise(DBURL) ; porpoised = true }
		sql = conn.getConnection()
		cleanUpDatabase()
		injector = Guice.createInjector(
			new DefaultTestModule(DBURL)
			, new ProjectModule()
			, new ScanModule()
		)
		onPostSetup()
	}
	
	private void cleanUpDatabase() {
		[
			'Scan'
			, 'Project'
		].each {
			sql.execute("delete from ${it}".toString())
		}
	}
}