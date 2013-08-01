package starslam.web.project

import starslam.web.WebTestBase

import com.greenmoonsoftware.tea.Tea

class PostTest extends WebTestBase {
	public void test_Success() {
		new Tea("http://localhost:5050")
			.post('/project', [
					name:"Test1"
					, rootPath:"C:/whatever"
				])
			.expectStatus(200)
			.verifyResponse { json ->
				assert json.success
				assert json.id != null
			}
			.brew()
	}
	
	public void test_DuplicateName() {
		def body = [
					name:"Test1"
					, rootPath:"C:/whatever"
				]
		
		new Tea("http://localhost:5050")
			.post('/project', body)
			.expectStatus(200)
		.brew()
			
		new Tea("http://localhost:5050")
			.post('/project', body)
			.expectStatus(400)
			.verifyResponse { json ->
				assert json.success == false
				assert json.errorCode == 'DUPLICATE_PROJECT_NAME'
				assert json.message.contains(body.name)
			}
			.brew()
	}
}
