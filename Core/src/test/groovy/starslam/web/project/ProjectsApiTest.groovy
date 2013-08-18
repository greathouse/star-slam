package starslam.web.project

import starslam.web.Kettle
import starslam.web.WebTestBase

import com.greenmoonsoftware.tea.Tea

class ProjectsApiTest extends WebTestBase {
	final String URL = '/projects'
	
	private def successfulProjectBody() {
		[
			name:"Test1"
			, rootPath:"C:/whatever"
			, fileGlob:"*.txt"
		]
	}
	
	public void test_Success() {
		Kettle.withTea { tea ->
			tea.post(URL, successfulProjectBody())
			.expectStatus(200)
			.verifyResponse { json ->
				assert json.success
				assert json.id != null
			}
			.verifyHeaders { headers ->
				assert headers."Location" != null
			}
		} 
	}
	
	public void test_DuplicateName() {
		def body = successfulProjectBody()
		
		Kettle.withTea { tea ->
			tea.post(URL, body)
			.expectStatus(200)
		}
			
		Kettle.withTea { tea ->
			tea.post(URL, body)
			.expectStatus(400)
			.verifyResponse { json ->
				assert json.success == false
				assert json.errorCode == 'DUPLICATE_PROJECT_NAME'
				assert json.message.contains(body.name)
			}
		}
	}
	
	public void test_Update() {
		def viewUrl
		def project = successfulProjectBody()
		Kettle.withTea { tea ->
			tea.post(URL, project)
			.expectStatus(200)
			.verifyHeaders { headers ->
				viewUrl = headers."Location"
			}
		}
		
		def updatedPath = "c:/updated"
		project.rootPath = project.rootPath+"-updatedPath"
		project.fileGlob = project.fileGlob+"-update"
		Kettle.withTea { tea ->
			tea.put(viewUrl, project)
			.expectStatus(200)
			.verifyResponse { json ->
				assert json.success
			}
		}
			
		Kettle.withTea { tea ->
			tea.get(viewUrl)
			.expectStatus(200)
			.verifyResponse { json ->
				json.rootPath == updatedPath
			}
		}
	}
	
	private void create(String name) {
		def project = successfulProjectBody()
		project.name = name
		project.rootPath="c:/${name}".toString()
		Kettle.withTea { Tea tea ->
			tea.post(URL, project)
			.expectStatus(200)
		}
	}
	
	public void test_List() {
		create("test 1")
		create("test 2")
		create("test 3")
		
		Kettle.withTea { tea ->
			tea.get(URL)
			.expectStatus(200)
			.verifyResponse { json ->
				assert json.size() == 3
			}
		}
	}
}
