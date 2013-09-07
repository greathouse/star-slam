package starslam
import groovy.sql.Sql
import junit.framework.TestCase

import com.google.common.io.Files

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
	protected static File pluginDirectory
	
	public final void setUp() {
		onPreSetup()
		if (!porpoised) { new Bootstrapper().porpoise(DBURL) ; porpoised = true }
		sql = conn.getConnection()
		cleanUpDatabase()
		if (!wired) {
			pluginDirectory = Files.createTempDir()
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