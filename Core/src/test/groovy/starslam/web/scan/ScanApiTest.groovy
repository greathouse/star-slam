package starslam.web.scan

import starslam.AsyncAssert
import starslam.web.Kettle
import starslam.web.WebTestBase

class ScanApiTest extends WebTestBase {
	
	private String setupWorkspace(int numberOfFiles) {
		def root = rootPath()
		(1..numberOfFiles).each {
			createFile(root, ".txt")
		}
		
		def projectUrl
		Kettle.withTea { tea ->
			tea.post('/projects', [
				name:"Test-"+UUID.randomUUID()
				, rootPath:root.canonicalPath
				, fileGlob:"*.txt"
			])
			.expectStatus(200)
			.verifyHeaders { headers ->
				projectUrl = headers."Location"
			}
		}
		return projectUrl
	}
	
	public void test_InitiateAndGet() {
		def numberOfFiles = randomInt(1,10)
		println "Number of files: ${numberOfFiles}"
		def projectUrl = setupWorkspace(numberOfFiles)
		
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
				assert json.rootPath != null
				assert json.status != null
				assert json.processingTime != null
				assert json.initiatedTime != null
			}
		}

		AsyncAssert.run {
			Kettle.withTea { tea ->
				tea.get(projectUrl+"/scans")
					.expectStatus(200)
					.verifyResponse { json ->
						assert json.size() == 1
						assert json[0].status == 'COMPLETED'
						assert json[0].fileCount == numberOfFiles
				}
			}
		}
	}
}
