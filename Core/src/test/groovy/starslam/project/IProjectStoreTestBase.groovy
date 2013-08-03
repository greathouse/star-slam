package starslam.project

import starslam.TestBase

abstract class IProjectStoreTestBase extends TestBase {
	IProjectStore impl
	
	protected abstract IProjectStore createInstance()
	
	protected void onPostSetup() {
		impl = createInstance()
	}
	
	private void saveRetrieveUpdate(Project persist) {
		def projectId = impl.persist(persist)
		def retrieve = impl.retrieve(projectId)
		
		assert retrieve != null
		assert persist.is(retrieve) == false
		assert persist.name == retrieve.name
		assert persist.rootPath == retrieve.rootPath
		
		def saveMe = new Project([id:retrieve.id, name:"Updated-"+UUID.randomUUID().toString(), rootPath:"C:/updated"])
		def updateId = impl.persist(saveMe)
		assert projectId == updateId

		def updated = impl.retrieve(projectId)
		
		assert saveMe.is(updated) == false
		assert saveMe.name == updated.name
		assert saveMe.rootPath == updated.rootPath
	}
	
	public void testCreateReadUpdate() {
		def persist = new Project([
			name:"Test Name"
			, rootPath:"C:/here"
		])
		
		saveRetrieveUpdate(persist)		
	}
	
	public void testCreateMultiple() {
		def persist1 = new Project([
			name:"Test Name"
			, rootPath:"C:/first"
		])
		
		def persist2 = new Project([
			name:"Second"
			, rootPath:"C:/second"
		])
		
		saveRetrieveUpdate(persist1)
		saveRetrieveUpdate(persist2)
	}
	
	public void test_SameProjectName_ThrowsException() {
		def saveTwice = new Project([
			name:"Test Name"
			, rootPath:"C:/first"
		])
		
		impl.persist(saveTwice)
		
		try {
			impl.persist(saveTwice)
			assert !true, "Should have thrown an exception"
		}
		catch (DuplicateProjectNameException e) {
			assert true
		}
	}
	
	private Project create(String name) {
		def rtn = new Project([name:name, rootPath:"c:/${name}"])
		impl.persist(rtn)
		return rtn
	}
	
	public void test_List() {
		def e1 = create("Test 1")
		def e2 = create("Test 2")
		def e3 = create("Test 3")
		
		def actual = impl.list()
		
		assert actual.size == 3
		assert actual[0].name == e1.name
		assert actual[1].name == e2.name
		assert actual[2].name == e3.name
	}
}
