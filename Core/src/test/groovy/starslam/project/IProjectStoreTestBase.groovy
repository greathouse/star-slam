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
		assert persist.created == retrieve.created
		assert persist.rootPath == retrieve.rootPath
		
		def saveMe = new Project([id:retrieve.id, name:"Updated-"+UUID.randomUUID().toString(), created:retrieve.created, rootPath:"C:/updated"])
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
			, created: new Date()
			, rootPath:"C:/here"
		])
		
		saveRetrieveUpdate(persist)		
	}
	
	public void testCreateMultiple() {
		def persist1 = new Project([
			name:"Test Name"
			, created: new Date()
			, rootPath:"C:/first"
		])
		
		def persist2 = new Project([
			name:"Second"
			, created: new Date()
			, rootPath:"C:/second"
		])
		
		saveRetrieveUpdate(persist1)
		saveRetrieveUpdate(persist2)
	}
	
	public void test_SameProjectName_ThrowsException() {
		def saveTwice = new Project([
			name:"Test Name"
			, created: new Date()
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
}
