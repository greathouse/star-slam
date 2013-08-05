package starslam.scan

import groovy.transform.Immutable;

@Immutable
class ScannedFile {
	String id
	String scanId
	String filename
	String relativePath
	String fullPath
	boolean isNew
	boolean hasChanged
	String data
	String scannerPlugin
	String md5
}
