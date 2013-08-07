package starslam.scan.plugin

import starslam.TestBase
import starslam.scan.plugins.IPluginService

import com.google.common.io.Files

class IPluginServiceTest extends TestBase {
	IPluginService impl
	
	protected void onPostSetup() {
		impl = injector.getInstance(IPluginService)
	}
	
	private File createFile(String name, String data) {
		def directory = Files.createTempDir()
		def file = new File(directory, name)
		file.withWriter { w -> w.write(data) }
		return file
	}
	
	public void test_Get_Txt_ShouldReturnDefaultTextFilePlugin() {
		def file = createFile("sample.txt", "This is my sample text.")
		
		def actual = impl.get(file)
		
		assert actual != null
		assert actual.name == 'internal-txt.v1'
	}
	
	public void test_Get_Xml_ShouldReturnDefaultXmlFilePlugin() {
		def file = createFile("sample.txt", "This is my sample text.")
		
		def actual = impl.get(file)
		
		assert actual != null
		assert actual.name == 'internal-xml.v1'
	}
}
