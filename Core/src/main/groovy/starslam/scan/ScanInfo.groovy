package starslam.scan

import groovy.transform.Immutable;

@Immutable
class ScanInfo {
	String id
	String projectId
	Date initiatedTime
	Date completionTime
	Date productionDate
	String rootPath
	long processingTime
	ScanStatus status
}
