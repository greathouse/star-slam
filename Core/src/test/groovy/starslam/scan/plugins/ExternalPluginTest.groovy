package starslam.scan.plugins

import groovy.json.JsonOutput
import org.junit.Ignore
import org.junit.Test
import starslam.TestBase


class ExternalPluginTest extends TestBase {
	ExternalPlugin impl
	File executableFile
	
	@Override
	protected void onPostSetup() {
		def exeStream = ClassLoader.getSystemResourceAsStream("FileInfo.exe")
		executableFile = File.createTempFile("FileInfo", ".exe")
		executableFile.withOutputStream { out ->
			out << exeStream
		} 
		
		def executablePath = executableFile.canonicalPath
		assert executablePath != null
		
		impl = new ExternalPlugin("fileinfo.v1", executablePath)
	}

	@Ignore("Needs to run on Windows machine")
	@Test
	public void test_FileInfo_GeneratesOutput() {
		def actual = impl.process(executableFile)
		
		assert actual != null
		assert actual.data != null
		assert JsonOutput.toJson(actual.data)
	}
}
