package starslam.scan

import starslam.AsyncAssert
import starslam.TestBase
import starslam.project.IProjectStore
import starslam.project.Project

class IScanServiceTest extends TestBase {
	private IScanService impl
	private IProjectStore projectStore
	private IScanStore scanStore
	
	protected void onPostSetup() {
		impl = injector.getInstance(IScanService)
		projectStore = injector.getInstance(IProjectStore)
		scanStore = injector.getInstance(IScanStore)
	}
	
	private String createProject(String rootPath) {
		return createProject(rootPath, "*.txt")
	}
	
	private String createProject(String rootPath, String fileGlob) {
		return projectStore.persist(new Project(null, UUID.randomUUID().toString(), rootPath, fileGlob))
	}
	
	public void test_Initiate_ShouldReturnScanInfo_AndBeRetrievableFromTheScanStore() {
		def rootPath = rootPath().canonicalPath
		def fileGlob = "*.exe"
		def projectId = createProject(rootPath, fileGlob)
		
		def actual = impl.initiate(projectId, {}, {}, {})
		
		assert actual != null
		assert projectId == actual.projectId
		assert actual.initiatedTime != null
		assert rootPath == actual.rootPath
		assert fileGlob == actual.fileGlob
		
		def retrieved = scanStore.retrieveLatestScanForProject(projectId)
		assert retrieved != null
		assert actual.id == retrieved.id
	}
	
	public void test_Initiate_ShouldCallOnBegin() {
		def calledClosure = false
		def scanInfo = null
		
		def projectId = createProject(rootPath().toString())
		impl.initiate(projectId, { x -> calledClosure = true; scanInfo = x }, {},{})
		
		assert calledClosure
		assert scanInfo != null
		assert ScanStatus.IN_PROGRESS ==  scanInfo.status
	}
	
	public void test_Initiate_ShouldCallOnComplete() {
		def calledClosure = false
		def scanInfo = null
		
		def projectId = createProject(rootPath().toString())
		impl.initiate(projectId, {}, {},{x -> calledClosure = true; scanInfo = x })
		
		AsyncAssert.run {
			assert calledClosure
			assert scanInfo != null
			assert scanInfo.status == ScanStatus.COMPLETED
			
			def retrieved = scanStore.retrieveScan(scanInfo.id)
			assert retrieved != null
			assert retrieved.status == ScanStatus.COMPLETED
			assert retrieved.processingTime > 0
		}
	}
	
	public void test_Initiate_ShouldCallAfterFile() {
		def path = rootPath()
		def file = createFile(path, ".txt")
		def projectId = createProject(path.toString())
						
		def filecount = 0
		def scannedFile = null
		def actual = impl.initiate(projectId, {}, { x -> filecount++ ; scannedFile = x }, {})
		
		AsyncAssert.run {
			assert filecount == 1
			assert scannedFile != null
			assert actual.id == scannedFile.scanId
			assert file.name == scannedFile.filename
			assert file.canonicalPath == scannedFile.fullPath
			assert scannedFile.isNew
			assert scannedFile.hasChanged == false
			assert scannedFile.md5 != null
		}
	}
	
	public void test_Initiate_WithMultipleFilesInRoot_ShouldCallAfterFile() {
		def path = rootPath()
		def files = []
		files << createFile(path, ".txt")
		files << createFile(path, ".txt")
		files << createFile(path, ".txt")
		files << createFile(path, ".txt")
		
		def projectId = createProject(path.toString())
		def afterFiles = []
						
		def filecount = 0
		def scannedFile = null
		def actual = impl.initiate(projectId, {}, { x -> afterFiles << x }, {})
		
		AsyncAssert.run {
			assert afterFiles.size() == files.size()
		}
	}
	
	public void test_Initiate_WithFilesInSubDirectories_ShouldCallAfterFile() {
		def path = rootPath()
		def file = createFile(path, "subdir", ".txt")
		def projectId = createProject(path.toString())
						
		def filecount = 0
		def scannedFile = null
		def actual = impl.initiate(projectId, {}, { x -> filecount++ ; scannedFile = x }, {})
		
		AsyncAssert.run {
			assert filecount == 1
		}
	}
	
	public void test_Initiate_MultipleInitiates_ScannedFileShouldNotBeNewOrChanged() {
		def path = rootPath().canonicalPath
		def file = createFile(path, ".txt")
		def projectId = createProject(path.toString())
		def setupDone = false
		impl.initiate(projectId, {}, {x -> setupDone = true}, {}) //first time
		AsyncAssert.run {
			assert setupDone
		}
		
		def scannedFile = null
		impl.initiate(projectId, {}, { x -> scannedFile = x }, {})
		
		AsyncAssert.run {
			assert scannedFile != null
			assert scannedFile.isNew == false
			assert scannedFile.hasChanged == false
		}
	}
}
