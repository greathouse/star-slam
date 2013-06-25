import starslam.Arguments
import starslam.ScanService
import starslam.Session


class ScanProjectTest extends TestBase {
	
	void test_scan_shouldSaveProjectAndScanAttributes() {
		Session.args = new Arguments() 
		Session.args.with {
			projectName = "Test Project " + UUID.randomUUID().toString()
			projectRoot = /c:\project\root/
			configFilePattern = "*.xml"
			sqlFileDirectory = "SQL.Migration"
		}
		Session.sql = sql
		
		def scanService = new ScanService()
		scanService.scan()
		
		def latestScan = scanService.latestScan(Session.args.projectName)
		assert latestScan!= null
		assert Session.args.projectName == latestScan.name
		assert latestScan.created != null
		
		assert Session.args.projectRoot == latestScan.projectRoot
		assert Session.args.configFilePattern == latestScan.configFilePattern
		assert Session.args.sqlFileDirectory == latestScan.sqlFileDirectory
	}
}