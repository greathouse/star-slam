package starslam.scan

import org.junit.Before
import org.junit.Test
import starslam.AsyncAssert
import starslam.DatabaseTestHelper

import static starslam.FileTestHelper.*
import starslam.project.Project
import starslam.project.ProjectStore

class ScanServiceTest {
	private ScanService impl
	private ProjectStore projectStore
	private ScanStore scanStore

    @Before
	public void onSetup() {
        def context = ScanContextTestHelper.wireContext()
        DatabaseTestHelper.setup(context)
		impl = context.getBean(ScanService)
		projectStore = context.getBean(ProjectStore)
		scanStore = context.getBean(ScanStore)
	}
	
	private String createProject(String rootPath) {
		return createProject(rootPath, "*.txt")
	}
	
	private String createProject(String rootPath, String fileGlob) {
		return projectStore.persist(new Project(null, UUID.randomUUID().toString(), rootPath, fileGlob))
	}

	@Test
	public void initiate_ShouldReturnScanInfo_AndBeRetrievableFromTheScanStore() {
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

	@Test
	public void initiate_ShouldCallOnBegin() {
		def calledClosure = false
		def scanInfo = null
		
		def projectId = createProject(rootPath().toString())
		impl.initiate(projectId, { x -> calledClosure = true; scanInfo = x }, {},{})
		
		assert calledClosure
		assert scanInfo != null
		assert ScanStatus.IN_PROGRESS ==  scanInfo.status
	}

//	@Ignore
//	@Test
//	public void initiate_NoFiles_ShouldCallOnComplete() {
//		def calledClosure = false
//		def scanInfo = null
//
//		def projectId = createProject(rootPath().toString())
//		pluginService.initiate(projectId, {}, {},{x -> calledClosure = true; scanInfo = x })
//
//		AsyncAssert.run {
//			assert calledClosure
//			assert scanInfo != null
//			assert scanInfo.status == ScanStatus.COMPLETED
//
//			def retrieved = scanStore.retrieveScan(scanInfo.id)
//			assert retrieved != null
//			assert retrieved.status == ScanStatus.COMPLETED
//			assert retrieved.processingTime > 0
//			assert retrieved.completionTime != null
//		}
//	}

	@Test
	public void initiate_ShouldCallOnComplete() {
		def calledClosure = false
		def scanInfo = null

		def path = rootPath()
		def projectId = createProject(path.toString())
		createFile(path, ".txt")

		impl.initiate(projectId, {}, {},{x -> calledClosure = true; scanInfo = x })

		AsyncAssert.run {
			assert calledClosure
			assert scanInfo != null
			assert scanInfo.status == ScanStatus.COMPLETED

			def retrieved = scanStore.retrieveScan(scanInfo.id)
			assert retrieved != null
			assert retrieved.status == ScanStatus.COMPLETED
			assert retrieved.processingTime > 0
			assert retrieved.completionTime != null
		}
	}

	@Test
	public void initiate_ShouldCallAfterFile() {
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

	@Test
	public void initiate_WithMultipleFilesInRoot_ShouldCallAfterFile() {
		def path = rootPath()
		def files = []
		def validExtension = ".txt"
		files << createFile(path, validExtension)
		files << createFile(path, validExtension)
		files << createFile(path, validExtension)
		files << createFile(path, validExtension)
		def nonMatchFiles = [] 
		nonMatchFiles << createFile(path, ".exe")
		
		def projectId = createProject(path.toString(), "*"+validExtension)
		def afterFiles = []
						
		def filecount = 0
		def scannedFile = null
		def actual = impl.initiate(projectId, {}, { x -> afterFiles << x }, {})
		
		AsyncAssert.run {
			assert afterFiles.size() == files.size()
		}
	}

	@Test
	public void initiate_WithFilesInSubDirectories_ShouldCallAfterFile() {
		def path = rootPath()
		def file = createFile(path, "subdir", ".txt")
		def projectId = createProject(path.toString(), '/subdir/*.txt')
						
		def filecount = 0
		def scannedFile = null
		def actual = impl.initiate(projectId, {}, { x -> filecount++ ; scannedFile = x }, {})
		
		AsyncAssert.run {
			assert filecount == 1
		}
	}

	@Test
	public void initiate_MultipleInitiates_ScannedFileShouldNotBeNewOrChanged() {
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

	@Test
	public void initiate_WithMultipleGlobPattern_ShouldCallAfterFile() {
		def path = rootPath()
		def files = []
		def validExtension1 = ".txt"
		files << createFile(path, validExtension1)
		
		def validExtension2 = ".properties"
		files << createFile(path, validExtension2)
		
		def subdir = "subdir"
		def fullfile = createFile(path, subdir, ".dll")
		files << fullfile
		println "Full Filename: $fullfile.name"

		def nonMatchFiles = []
		nonMatchFiles << createFile(path, ".exe")
		
		def projectId = createProject(path.toString(), "*"+validExtension1+"|*"+validExtension2+"|"+subdir+'/'+fullfile.name)
		def afterFiles = []

		impl.initiate(projectId, {}, { x -> afterFiles << x }, {})
		
		AsyncAssert.run {
			assert afterFiles.size() == files.size()
		}
	}

	@Test
	public void initiate_SpecificFileWithoutLeadingSlash_ShouldCallAfterFile() {
		def path = rootPath()
		def files = []

		def subdir = "subdir"
		def fullfile = createFile(path, subdir, ".dll")
		files << fullfile
		println "Full Filename: $fullfile.name"

		def nonMatchFiles = []
		nonMatchFiles << createFile(path, ".exe")

		def projectId = createProject(path.toString(), subdir+'/'+fullfile.name)
		def afterFiles = []

		def filecount = 0
		def scannedFile = null
		def actual = impl.initiate(projectId, {}, { x -> afterFiles << x }, {})

		AsyncAssert.run {
			assert afterFiles.size() == files.size()
		}
	}
}
