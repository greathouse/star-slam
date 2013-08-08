package starslam.scan.plugins

import static groovy.json.JsonOutput.toJson
import starslam.TestBase

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
	
	private File createPlugin(String name, String filetype, String executable) {
		def json = [
			filetype:filetype
			, name:name
			, executable:executable
		]
		def subdir = new File(pluginDirectory, name)
		subdir.mkdirs()
		new File(subdir, "plugin.json").withWriter { w -> w.write(toJson(json)) }
		return subdir
	}
	
	public void test_Get_Txt_ShouldReturnDefaultTextFilePlugin() {
		def file = createFile("sample.txt", "This is my sample text.")
		
		def actual = impl.get(file)
		
		assert actual != null
		assert actual.name == 'internal-txt.v1'
	}
	
	public void test_Get_Xml_ShouldReturnDefaultXmlFilePlugin() {
		def file = createFile("sample.xml", "<xml></xml>")
		
		def actual = impl.get(file)
		
		assert actual != null
		assert actual.name == 'internal-xml.v1'
	}
	
	public void test_Get_ExternalPlugin_ShouldLoadAndReturnExternalPlugin() {
		def pluginName = 'external.v1'
		def filetype = 'dll'
		def file = createFile("some.${filetype}", "0010101011110101001001010010100101001")
		createPlugin(pluginName, filetype, 'c:/some/extension.exe')
		
		def actual = impl.get(file)
		
		assert actual != null
		assert pluginName == actual.name
	}
	
	private File copyPluginExecutable() {
		def pluginSubdir = createPlugin("FileInfo.v1", "exe", "FileInfo.exe")
		def exeStream = ClassLoader.getSystemResourceAsStream("FileInfo.exe")
		def executableFile = new File(pluginSubdir, "FileInfo.exe")
		executableFile.withOutputStream { out ->
			out << exeStream
		}
		
		return executableFile
	}
	
	public void test_ExternalPlugin_RelativePathInJsonFile_ShouldStillExecute() {
		def executable = copyPluginExecutable()
		
		def plugin = impl.get(executable)
		assert plugin != null
		
		def actual = plugin.process(executable)
		
		assert actual != null
		assert actual.data != null
	}
}
