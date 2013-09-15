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
		scanStore = context.getBean(IScanStore)
		projectStore = context.getBean(IProjectStore)
	}

	private String createProject() {
		return projectStore.persist(new Project(null, UUID.randomUUID().toString(), Files.createTempDir().toString(), UUID.randomUUID().toString()))
	}
	
	private String createScan(String projectId, Date initiated) {
		return scanStore.persist(new ScanInfo([
			projectId:projectId
			, initiatedTime:initiated
			, completionTime:new Date().plus(1)
			, productionDate:new Date().plus(2)
			, rootPath:"rootPath"
			, processingTime:System.currentTimeMillis()
			, status:ScanStatus.COMPLETED
			, fileGlob:"jar,war,ear,properties,xml"
			, fileCount:69
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
		assert expected.fileGlob == actual.fileGlob
		assert expected.fileCount == actual.fileCount
	}

	public void test_PersistAndRetrieve_Success() {
		def projectId = createProject()
		createScan(projectId, new Date().plus(1))
		
		def saveMe = new ScanInfo([
			projectId:projectId
			, initiatedTime:new Date()
			, completionTime:new Date().plus(1)
			, productionDate:new Date().plus(2)
			, rootPath:"rootPath"
			, processingTime:System.currentTimeMillis()
			, status:ScanStatus.COMPLETED
			, fileGlob:"jar,war,ear,properties,xml"
			, fileCount: 69
		])
		
		def scanId = scanStore.persist(saveMe)
		
		assertScan(scanId, saveMe, scanStore.retrieveScan(scanId))
	}
	
	public void test_Persist_WithOptionalFields_ShouldBeRetrieved() {
		def projectId = createProject()
		def expected = new ScanInfo([
			projectId:projectId
			, initiatedTime:new Date()
			, rootPath:"c:/"
			, status:ScanStatus.IN_PROGRESS
			, fileGlob:"jar,war,ear,properties,xml"
		])
		def scanId = scanStore.persist(expected)
		
		def actual = scanStore.retrieveScan(scanId)
		
		assert actual != null
		assert actual.processingTime == 0
		assert actual.productionDate  == null
		assert actual.completionTime == null
	}
	
	public void test_Persist_AndUpdate_ShouldOnlyCreateOneScanForTheProject() {
		def projectId = createProject()
		def expected = new ScanInfo([
			projectId:projectId
			, initiatedTime:new Date()
			, rootPath:"c:/"
			, status:ScanStatus.IN_PROGRESS
			, fileGlob:"jar,war,ear,properties,xml"
		])
		def scanId = scanStore.persist(expected)
		def retrieved = scanStore.retrieveScan(scanId)
		
		def secondId = scanStore.persist(retrieved)
		assert scanId == secondId
		
		def actualProjectScans = scanStore.scansForProject(projectId)
		assert 1 == actualProjectScans.size()
	}
	
	public void test_Retrieve_NotFound_ShouldReturnNull() {
		def actual = scanStore.retrieveScan(UUID.randomUUID().toString())
		assert actual == null
	}
	
	public void test_RetrieveLatestScanForProject_Success() {
		def projectId = createProject()
		createScan(projectId, new Date().plus(-1))
		createScan(createProject(), new Date().plus(2))
		
		def expected = new ScanInfo([
			projectId:projectId
			, initiatedTime:new Date()
			, completionTime:new Date().plus(1)
			, productionDate:new Date().plus(2)
			, rootPath:"rootPath"
			, processingTime:System.currentTimeMillis()
			, status:ScanStatus.COMPLETED
			, fileGlob:"jar,war,ear,properties,xml"
		])
		def expectedId = scanStore.persist(expected)
		
		def actual = scanStore.retrieveLatestScanForProject(projectId)
		
		assert actual != null
		assert expected != actual
		assertScan(expectedId, expected, actual)
	}
	
	public void test_RetrieveLatestScanForProject_NotFound_ShouldReturnNull() {
		def projectId = createProject()
		def actual = scanStore.retrieveLatestScanForProject(projectId)
		assert actual == null
	}
	
	public void test_ScansForProject_ShouldOrderByDecendingDate() {
		def projectId = createProject()
		def first = createScan(projectId, new Date().plus(-2))
		def second = createScan(projectId, new Date().plus(-1))
		def third = createScan(projectId, new Date())
		
		def actual = scanStore.scansForProject(projectId)
		
		assert actual != null
		assert actual.size == 3
		assert third == actual[0].id
		assert second == actual[1].id
		assert first == actual[2].id
	}
	
	public void test_ScansForProject_NoScans_ShouldReturnEmptyIterable() {
		def projectId = createProject()
		def actual = scanStore.scansForProject(projectId)
		assert actual.size == 0
	}
	
	private ScannedFile createScannedFile(String scanId, String relativePath, String filename) {
		def rtn = new ScannedFile([
			scanId:scanId
			, filename:filename
			, relativePath:relativePath+filename
			, fullPath:"c:/${relativePath}${filename}"
			, isNew:true
			, hasChanged:false
			, data:'{"some":"json"}'
			, scannerPlugin:"default-text.v1"
			, md5:"1234567890"
		])
		scanStore.persist(rtn)
		return rtn
	}
	
	private void assertScannedFile(ScannedFile expected, ScannedFile actual) {
		assert actual != null
		assert expected != actual
		assert expected.scanId == actual.scanId
		assert expected.filename == actual.filename
		assert expected.relativePath == actual.relativePath
		assert expected.fullPath == actual.fullPath
		assert expected.isNew == actual.isNew
		assert expected.hasChanged == actual.hasChanged
		assert expected.data == actual.data
		assert expected.scannerPlugin == actual.scannerPlugin
		assert expected.md5 == actual.md5
	}
	
	public void test_ScannedFile_PersistAndRetrieveByRelativePath_ShouldReturnLatestFileFromScans() {
		def projectId = createProject()
		
		def subdir = "subdir/"
		def filename = "Test.txt"
		createScannedFile(createScan(createProject(), new Date().plus(1)), subdir, filename) //negative project
		createScannedFile(createScan(projectId, new Date().plus(-1)), subdir, filename) //negative old scan
		
		def scanId = createScan(projectId, new Date())
		createScannedFile(scanId, subdir, "negative.txt") //negative filename
		createScannedFile(scanId, "negative", filename) //negative directory
		def saveMe = createScannedFile(scanId, subdir, filename)
		
		
		def actual = scanStore.retrieveLatestScannedFileWithRelativePath(projectId, saveMe.relativePath)
		
		assertScannedFile(saveMe, actual)
	}
	
	public void test_ScannedFile_RetrieveByRelativePath_NotFound_ShouldReturnNull() {
		def projectId = createProject()
		def actual = scanStore.retrieveLatestScannedFileWithRelativePath(projectId, "NotFound.txt")
		assert actual == null
	}
	
	public void test_FilesForScan_ShouldReturnFileSortedByPath() {
		def projectId = createProject()
		def scanId = createScan(projectId, new Date())
		
		createScannedFile(createScan(createProject(), new Date().plus(1)), "negative/", "negative") //negative project
		createScannedFile(createScan(projectId, new Date().plus(-1)), "negative/", "negative") //negative old scan
		
		def expectedFourth = createScannedFile(scanId, "z_first/", "first.txt")
		def expectedThird = createScannedFile(scanId, "z_first/", "a_first.txt")
		def expectedFirst = createScannedFile(scanId, "a_second/", "second.txt")
		def expectedSecond = createScannedFile(scanId, "m_third", "third.txt")
		
		def actual = scanStore.filesForScan(scanId)
		
		assert actual != null
		assert actual.size == 4
		assertScannedFile(expectedFirst, actual[0])
		assertScannedFile(expectedSecond, actual[1])
		assertScannedFile(expectedThird, actual[2])
		assertScannedFile(expectedFourth, actual[3])
		
		def actualCount = scanStore.filesForScanCount(scanId)
		assert actualCount == 4
	}
	
	public void test_FilesForScan_NoFiles_ShouldReturnEmptyList() {
		def scanId = createScan(createProject(), new Date())
		def actual = scanStore.filesForScan(scanId)
		assert actual != null
		assert actual.size == 0
	}
}
