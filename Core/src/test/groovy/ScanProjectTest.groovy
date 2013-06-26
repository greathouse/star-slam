import starslam.RunScanArguments
import starslam.ScanService
import starslam.Session


class ScanProjectTest extends TestBase {
	private void writeFile(String path, String contents) {
		new File(path).withWriter { writer ->
			writer.writeLine(contents)
		}
	}
	
	void test_scan_shouldSaveProjectAndScanAttributes() {
		def samplePath = /\src\test\resources\SimpleProject/
		def workingDir = System.getProperty("user.dir")
		def args = new RunScanArguments() 
		args.with {
			projectName = "Test Project " + UUID.randomUUID().toString()
			projectRoot = workingDir+samplePath
			configFilePattern = "*.config"
			sqlFileDirectory = "SQL.Migration"
		}
		Session.sql = sql
		
		def scanService = new ScanService()
		scanService.scan(args)
		
		def latestScan = scanService.latestScan(args.projectName)
		assert latestScan!= null
		assert args.projectName == latestScan.name
		assert latestScan.created != null
		
		assert args.projectRoot == latestScan.projectRoot
		assert args.configFilePattern == latestScan.configFilePattern
		assert args.sqlFileDirectory == latestScan.sqlFileDirectory
		assert latestScan.deployTime != null
		
		assert latestScan.configFiles != null
		assert latestScan.configFiles.size == 1
		
		def configFile = latestScan.configFiles[0]
		assert configFile.name == args.projectRoot+/\web.config/
		assert configFile.md5 != null
		assert configFile.isNew
		assert configFile.hasChanged == false
		
	}
	
	void test_scan_withConfigFileChange_shouldIndicateNotNewAndHasChanged() {
		def samplePath = /\src\test\resources\MutatingProject/
		def workingDir = System.getProperty("user.dir")
		def args = new RunScanArguments()
		args.with {
			projectName = "Test Project " + UUID.randomUUID().toString()
			projectRoot = workingDir+samplePath
			configFilePattern = "*.config"
			sqlFileDirectory = "SQL.Migration"
		}
		Session.sql = sql
		
		def pathToConfig = args.projectRoot+/\web.config/
		writeFile(pathToConfig, "Initial content")
		
		def scanService = new ScanService()
		
		def assertConfigFile = { isNew, hasChanged ->
			def latestScan = scanService.latestScan(args.projectName)
			assert latestScan.configFiles != null
			assert latestScan.configFiles.size == 1
			
			def configFile = latestScan.configFiles[0]
			assert configFile.name == pathToConfig
			assert configFile.isNew == isNew
			assert configFile.hasChanged == hasChanged
		}
		
		scanService.scan(args)
		assertConfigFile(true, false)
		
		writeFile(pathToConfig, "Updated Content")
		scanService.scan(args)
		assertConfigFile(false, true)
	}
}