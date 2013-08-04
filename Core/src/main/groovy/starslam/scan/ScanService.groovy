package starslam.scan

import groovy.io.FileType

import java.nio.file.*
import java.security.MessageDigest

import starslam.IDbConnection
import starslam.project.IProjectStore

import com.google.inject.Inject

class ScanService implements IScanService {
	IDbConnection dbConnector
	IProjectStore projectStore
	
	@Inject
	public ScanService(IDbConnection conn, IProjectStore projectStore) {
		dbConnector = conn
		this.projectStore = projectStore
	}
	
	@Override
	public ScanInfo initiate(
		String projectId, 
		Closure<ScanInfo> onBegin,
		Closure<ScannedFile> afterFile, 
		Closure<ScanInfo> onComplete
	) {
		def id = UUID.randomUUID().toString()
		def project = projectStore.retrieve(projectId)
		
		def info = new ScanInfo([
				id:id
				, projectId:projectId
				, initiatedTime:new Date()
				, rootPath:project.rootPath
			])
		onBegin(info)
		
		new File(project.rootPath).eachFileRecurse(FileType.FILES) { file ->
			def scannedFile = new ScannedFile([
				scanId:id
				, filename:file.name
				, fullPath:file.canonicalPath
				, isNew:true
				, hasChanged:false
				, md5:generateMd5(file)
			])
			
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
