package starslam.web.scan

import starslam.web.Kettle
import starslam.web.WebTestBase

class ScanFilesApiTest extends WebTestBase {
	private String setupWorkspace() {
		def root = rootPath()
		createFile(root, ".txt")
		createFile(root, ".txt")
		
		def projectUrl
		Kettle.withTea { tea ->
			tea.post('/projects', [
				name:"Test1"
				, rootPath:root.canonicalPath
				, fileGlob:"*.txt"
			])
			.expectStatus(200)
			.verifyHeaders { headers ->
				projectUrl = headers."Location"
			}
		}
		
		def scanUrl
		Kettle.withTea { tea ->
			tea.post(projectUrl+"/scans",[:])
			.expectStatus(200)
			.verifyHeaders { headers ->
				scanUrl = headers."Location"
			}
		}
		return scanUrl
	}
	
	public void test_InitiateAndGet() {
		def scanUrl = setupWorkspace() 
		
		Kettle.withTea { tea ->
			tea.get(scanUrl+"/files")
			.expectStatus(200)
			.verifyResponse { json ->
				assert json.size() == 2
				
				def file = json[0]
				assert file.filename != null
				assert file.relativePath != null
				assert file.fullPath != null
				assert file.isNew
				assert file.hasChanged == false
			}
		}
	}
}
