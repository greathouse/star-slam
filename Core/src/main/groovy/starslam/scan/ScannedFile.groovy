package starslam.scan

import groovy.transform.Immutable;

@Immutable
class ScannedFile {
	String scanId
	String filename
	String fullPath
	boolean isNew
	boolean hasChanged
	String data
	String scannerPlugin
	String md5
}
