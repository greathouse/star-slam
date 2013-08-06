package starslam.scan

import groovy.io.FileType

import java.nio.file.*
import java.security.MessageDigest

import starslam.IDbConnection
import starslam.project.IProjectStore

import com.google.inject.Inject

class ScanService implements IScanService {
	final IDbConnection dbConnector
	final IProjectStore projectStore
	final IScanStore scanStore
	
	@Inject
	public ScanService(IDbConnection conn, IProjectStore projectStore, IScanStore scanStore) {
		dbConnector = conn
		this.projectStore = projectStore
		this.scanStore = scanStore
	}
	
	@Override
	public ScanInfo initiate(
		String projectId, 
		Closure<ScanInfo> onBegin,
		Closure<ScannedFile> afterFile, 
		Closure<ScanInfo> onComplete
	) {
		def project = projectStore.retrieve(projectId)
		
		def scanMap = [
				projectId:projectId
				, initiatedTime:new Date()
				, rootPath:project.rootPath
				, status:ScanStatus.IN_PROGRESS
			]
		def info = new ScanInfo(scanMap)
		scanMap.id = scanStore.persist(info)
		info = new ScanInfo(scanMap)
		onBegin(info)
		
		new File(project.rootPath).eachFileRecurse(FileType.FILES) { file ->
			def md5 = generateMd5(file)
			def relativePath = file.canonicalPath.replace(project.rootPath, '')
			def existing = scanStore.retrieveLatestScannedFileWithRelativePath(projectId, relativePath)
			
			def scannedFileMap = [
				scanId:info.id
				, filename:file.name
				, fullPath:file.canonicalPath
				, relativePath:relativePath
				, isNew:(existing == null)
				, hasChanged:(existing != null && existing.md5 != md5)
				, scannerPlugin:"default-txt"
				, data:"something for now"
				, md5:md5
			]
			def scannedFile = new ScannedFile(scannedFileMap)
			scannedFileMap.id = scanStore.persist(scannedFile)
			scannedFile = new ScannedFile(scannedFileMap)
			afterFile(scannedFile)
		}
		
		onComplete(info)
		return info
	}

	@Override
	public ScanStatus status(String scanId) {
		return null
	}

	@Override
	public ScanInfo retrieve(String scanId) {
		return null;
	}
	
	private def generateMd5(final file) {
		def digest = MessageDigest.getInstance("MD5")
		file.withInputStream(){is->
			byte[] buffer = new byte[8192]
			int read = 0
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
		}
		byte[] md5sum = digest.digest()
		BigInteger bigInt = new BigInteger(1, md5sum)
		return bigInt.toString(16)
	}
}
