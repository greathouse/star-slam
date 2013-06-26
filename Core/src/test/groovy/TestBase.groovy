import groovy.sql.Sql
import junit.framework.TestCase
import starslam.Bootstrapper

class TestBase extends TestCase {
	protected final def DBURL = 'jdbc:h2:~/star-slam-test'
	protected Sql sql
	
	protected void onPreSetup() {}
	protected void onPostSetup() {}
	
	private static porpoised = false;
	
	public final void setUp() {
		onPreSetup()
		if (!porpoised) { new Bootstrapper().porpoise(DBURL) ; porpoised = true }
		sql = Sql.newInstance(DBURL, '', '', 'org.h2.Driver')
		cleanUpDatabase()
		onPostSetup()
	}
	
	private void cleanUpDatabase() {
		[
			'ConfigFile'
			, 'Scan'
			, 'Project'
		].each {
			sql.execute("delete from ${it}".toString())
		}
	}
}