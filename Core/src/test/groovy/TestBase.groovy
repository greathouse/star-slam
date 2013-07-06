import groovy.sql.Sql
import junit.framework.TestCase
import starslam.Bootstrapper

abstract class TestBase extends TestCase {
	protected final def DBURL = 'jdbc:h2:~/star-slam-test'
	protected Sql sql
	protected Closure getConnection = {
		Sql.newInstance(DBURL, '', '', 'org.h2.Driver')
	}
	
	protected void onPreSetup() {}
	protected void onPostSetup() {}
	
	private static porpoised = false;
	
	public final void setUp() {
		onPreSetup()
		if (!porpoised) { new Bootstrapper().porpoise(DBURL) ; porpoised = true }
		sql = getConnection()
		cleanUpDatabase()
		onPostSetup()
	}
	
	private void cleanUpDatabase() {
		[
			'SqlFile'
			, 'ConfigFile'
			, 'Scan'
			, 'Project'
		].each {
			sql.execute("delete from ${it}".toString())
		}
	}
}