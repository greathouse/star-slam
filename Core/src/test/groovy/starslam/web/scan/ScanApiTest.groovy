package starslam.web.scan

import starslam.scan.ScanStatus
import starslam.web.Kettle
import starslam.web.WebTestBase

class ScanApiTest extends WebTestBase {
	
	private String setupWorkspace() {
		def root = rootPath()
		createFile(root, ".txt")
		return root
	}
	
	public void test_InitiateAndGet() {
		def root = setupWorkspace()
		def projectUrl
		
		Kettle.withTea { tea ->
			tea.post('/projects', [
				name:"Test1"
				, rootPath:root
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
				assert headers."Location" != null
				scanUrl = headers."Location"
			}	
			.verifyResponse { json ->
				assert json.id != null
			}
		}
		
		Kettle.withTea { tea ->
			tea.get(scanUrl)
			.expectStatus(200)
			.verifyResponse { json ->
				assert json.rootPath == root
				assert json.status != null
				assert json.processingTime != null
				assert json.initiatedTime != null
				assert json.numberOfFiles != null
			}
		}
	}
}
