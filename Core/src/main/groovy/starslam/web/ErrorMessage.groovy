package starslam.web

import groovy.transform.Immutable

@Immutable
class ErrorMessage {
	String code
	String property
	String message
}
