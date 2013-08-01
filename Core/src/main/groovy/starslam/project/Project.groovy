package starslam.project

import groovy.transform.Immutable

@Immutable
class Project {
	String id
	String name
	Date created
	String rootPath
}
