package starslam.scan

import starslam.TestBase
import starslam.project.IProjectStore
import starslam.project.Project

import com.google.common.io.Files

class IScanStoreTest extends TestBase {
	IScanStore scanStore
	IProjectStore projectStore
	
	@Override
	protected void onPostSetup() {
		scanStore = injector.getInstance(IScanStore)
		projectStore = injector.getInstance(IProjectStore)
	}

	private String createProject() {
		return projectStore.persist(new Project(null, UUID.randomUUID().toString(), Files.createTempDir().toString()))
	}
	
	private String negativeScan(String projectId, Date initiated) {
		return scanStore.persist(new ScanInfo([
			projectId:projectId
			, initiatedTime:initiated
			, completionTime:new Date().plus(1)
			, productionDate:new Date().plus(2)
			, rootPath:"rootPath"
			, processingTime:System.currentTimeMillis()
			, status:ScanStatus.COMPLETED
		]))
	}
	
	private void assertScan(String expectedId, ScanInfo expected, ScanInfo actual) {
		assert actual != null
		assert expected != actual
		assert expectedId == actual.id
		assert expected.projectId == actual.projectId
		assert expected.initiatedTime == actual.initiatedTime
		assert expected.completionTime == actual.completionTime
		assert expected.productionDate == actual.productionDate
		assert expected.processingTime == actual.processingTime
		assert expected.status == actual.status
	}

	public void test_PersistAndRetrieve_Success() {
		def projectId = createProject()
		negativeScan(projectId, new Date().plus(1))
		
		def saveMe = new ScanInfo([
			projectId:projectId
			, initiatedTime:new Date()
			, completionTime:new Date().plus(1)
			, productionDate:new Date().plus(2)
			, rootPath:"rootPath"
			, processingTime:System.currentTimeMillis()
			, status:ScanStatus.COMPLETED		
		])
		
		def scanId = scanStore.persist(saveMe)
		
		assertScan(scanId, saveMe, scanStore.retrieveScan(scanId))
	}
	
	public void test_Retrieve_NotFound_ShouldReturnNull() {
		def actual = scanStore.retrieveScan(UUID.randomUUID().toString())
		assert actual == null
	}
	
//	public void 
}
