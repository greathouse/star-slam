package starslam.web.project

import com.greenmoonsoftware.tea.Tea
import org.junit.Before
import org.junit.Test
import starslam.web.Kettle
import starslam.web.WebServerTestHelper

class ProjectsApiTest {
	final String URL = '/projects'

    @Before
    public void onSetup() {
        WebServerTestHelper.startServer()
    }

	private def successfulProjectBody() {
		[
			name:"Test1"
			, rootPath:"C:/whatever"
			, fileGlob:"*.txt"
		]
	}

	@Test
	public void success() {
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

	@Test
	public void duplicateName() {
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
				def error = json.errors.find {
					it.code == 'DUPLICATE_PROJECT_NAME'
				}
				assert error
				assert error.property == 'name'
				assert error.message.contains(body.name)
			}
		}
	}

	@Test
	public void update() {
		def viewUrl = ''
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

	@Test
	public void list() {
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

	@Test
	public void required() {
		Kettle.withTea { tea ->
			tea.post(URL, [:])
			.expectStatus(400)
			.verifyResponse { json ->
				assert json.success == false
				assert json.errors.size() == 3
				assert json.errors.findAll{it.code == 'REQUIRED_FIELD'}.size() == 3
				
				assert json.errors.find{it.property == 'name'}
				assert json.errors.find{it.property == 'rootPath'}
				assert json.errors.find{it.property == 'fileGlob'}
			}
		}
	}
}
