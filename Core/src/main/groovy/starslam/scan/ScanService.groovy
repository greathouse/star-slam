package starslam.scan

import groovy.io.FileType
import groovyx.gpars.actor.Actors
import org.springframework.util.AntPathMatcher

import java.nio.file.*
import java.security.MessageDigest

import starslam.IDbConnection
import starslam.project.IProjectStore

class ScanService implements IScanService {
	final IDbConnection dbConnector
	final IProjectStore projectStore
	final IScanStore scanStore
	
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
		def startTime = System.currentTimeMillis()
		def project = projectStore.retrieve(projectId)
		
		def previousFiles = [:]
		def lastScan = scanStore.retrieveLatestScanForProject(projectId)
		if (lastScan) {
			scanStore.filesForScan(lastScan.id).each { f ->
				previousFiles[f.relativePath] = f
			}
		}
		
		def scanMap = [
				projectId:projectId
				, initiatedTime:new Date()
				, rootPath:project.rootPath
				, status:ScanStatus.IN_PROGRESS
				, fileGlob:project.fileGlob
			]
		def info = new ScanInfo(scanMap)
		scanMap.id = scanStore.persist(info)
		info = new ScanInfo(scanMap)
		onBegin(info)

		def processFile = { File file ->
			def md5 = generateMd5(file)
			def relativePath = file.canonicalPath.replace(project.rootPath, '')
			def existing = previousFiles[relativePath]

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

		def globMatcher = new AntPathMatcher()
		globMatcher.setPathSeparator(System.getProperty("file.separator"))
		def fileConsumer = Actors.reactor { File file ->
			def fileName = file.canonicalPath.replaceAll('\\\\','/').substring(info.rootPath.length() + 1)
			if (info.fileGlob.split(/\|/).any {pattern -> globMatcher.match(pattern, fileName) }) {
				println "FileConsumer: Matches: " + file.canonicalPath
				processFile(file)
				println "FileConsumer: Processed: " + file.canonicalPath
			}
			reply file.canonicalPath
		}

		def directoryConsumer = Actors.actor {
			def files = [:]
			loop {
				react { dir ->
					println "DirectoryConsumer: Received: "+dir.canonicalPath
					dir.eachDir { subDir ->
						println "DirectoryConsumer: directoryConsumer->master: "+subDir.canonicalPath
						sender.send(subDir)
					}

					def replyChannel = sender
					dir.eachFile(FileType.FILES) { file ->
						files[file.canonicalPath] = false
						println 'DirectoryConsumer: Sending to fileConsumer: '+file.canonicalPath
						fileConsumer.sendAndContinue file, {
							println "DirectoryConsumer: continue: "+it
							files[file.canonicalPath] = true
							if (files.values().findIndexOf { it == false } < 0) {
								replyChannel.send "All Done"
							}
						}
						println "DirectoryConsumer: Sent to fileConsumer: " +file.canonicalPath
					}
				}
			}
		}

		def master = Actors.actor {
			loop {
				react {	dir ->
					switch(dir) {
						case File:
							println 'Master: Sending to directoryConsumer: '+dir.canonicalPath
							directoryConsumer.send(dir)
							break
						case String:
							println 'Master: Terminate'
							terminate()
					}
				}
			}
		}
		
		Thread.start {
//			def finder = new FileFinder(info.fileGlob, {f -> println f.canonicalFile ; fileConsumer.send f})
//			finder.execute(project.rootPath)
			master.send(new File(project.rootPath))
			//fileConsumer.send null

			master.join()
			println "Everyone's Processed"
			def endTime = System.currentTimeMillis()
			scanMap.status = ScanStatus.COMPLETED
			scanMap.processingTime = endTime - startTime
			scanMap.completionTime = new Date()
			scanMap.fileCount = scanStore.filesForScanCount(scanMap.id)
			def completeInfo = new ScanInfo(scanMap)
			scanStore.persist(completeInfo)
			onComplete(completeInfo)
		}
		
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
