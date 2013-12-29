package starslam.scan;

import groovy.lang.Closure;

public interface ScanService {
	ScanInfo initiate(
		String projectId, 
		Closure<ScanInfo> onBegin, 
		Closure<ScannedFile> afterFile, 
		Closure<ScanInfo> onComplete
	);
	ScanStatus status(String scanId);
	ScanInfo retrieve(String scanId);
}
