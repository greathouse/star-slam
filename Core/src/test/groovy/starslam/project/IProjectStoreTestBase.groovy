package starslam.project

import starslam.TestBase

abstract class IProjectStoreTestBase extends TestBase {
	IProjectStore impl
	
	protected abstract IProjectStore createInstance()
	
	protected void onPostSetup() {
		impl = createInstance()
	}
	
	public void testCreateAndRead() {
		def persist = new Project([
			name:"Test Name",
			created: new Date(),
			rootPath:"C:/here",
			configFilePattern:"*.xml",
			sqlFileDirectory:"Sql.Migration/"
		])
		
		def projectId = impl.persist(persist)
		
		def retrieve = impl.retrieve(projectId)
		
		assert persist != retrieve
		assert persist.name == retrieve.name
		assert persist.created == retrieve.created
		assert persist.rootPath == retrieve.rootPath
		assert persist.configFilePattern == retrieve.configFilePattern
		assert persist.sqlFileDirectory == retrieve.sqlFileDirectory 
	}
}
