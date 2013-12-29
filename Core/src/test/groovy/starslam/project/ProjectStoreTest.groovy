package starslam.project

import org.junit.Before
import org.junit.Test
import starslam.DatabaseTestHelper

class ProjectStoreTest {
	ProjectStore impl

    @Before
	public void onSetup() {
        def context = ProjectContextTestHelper.wireContext()
        DatabaseTestHelper.setup(context)
		impl = context.getBean(ProjectStore)
	}
	
	private void saveRetrieveUpdate(Project persist) {
		def projectId = impl.persist(persist)
		def retrieve = impl.retrieve(projectId)
		
		assert retrieve != null
		assert persist.is(retrieve) == false
		assert persist.name == retrieve.name
		assert persist.rootPath == retrieve.rootPath
		assert persist.fileGlob == retrieve.fileGlob
		
		def saveMe = new Project([id:retrieve.id, name:"Updated-"+UUID.randomUUID().toString(), rootPath:"C:/updated", fileGlob:"class"])
		def updateId = impl.persist(saveMe)
		assert projectId == updateId

		def updated = impl.retrieve(projectId)
		
		assert saveMe.is(updated) == false
		assert saveMe.name == updated.name
		assert saveMe.rootPath == updated.rootPath
		assert saveMe.fileGlob == updated.fileGlob
	}

	@Test
	public void createReadUpdate() {
		def persist = new Project([
			name:"Test Name"
			, rootPath:"C:/here"
			, fileGlob:"ear,jar,war,properties"
		])
		
		saveRetrieveUpdate(persist)		
	}

	@Test
	public void createMultiple() {
		def persist1 = new Project([
			name:"Test Name"
			, rootPath:"C:/first"
			, fileGlob:"ear,jar,war,properties"
		])
		
		def persist2 = new Project([
			name:"Second"
			, rootPath:"C:/second"
			, fileGlob:"ear,jar,war,properties"
		])
		
		saveRetrieveUpdate(persist1)
		saveRetrieveUpdate(persist2)
	}

	@Test
	public void sameProjectName_ThrowsException() {
		def saveTwice = new Project([
			name:"Test Name"
			, rootPath:"C:/first"
			, fileGlob:"ear,jar,war,properties"
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
		def rtn = new Project([name:name, rootPath:"c:/${name}", fileGlob:"ear,jar,war,properties"])
		impl.persist(rtn)
		return rtn
	}

	@Test
	public void list() {
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
