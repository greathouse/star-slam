package starslam.project

import groovy.transform.Immutable

@Immutable
class Project {
	String id
	String name
	String rootPath
	String fileGlob
}
