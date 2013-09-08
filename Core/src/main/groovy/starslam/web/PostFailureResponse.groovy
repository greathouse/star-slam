package starslam.web

import groovy.transform.Immutable

@Immutable
class PostFailureResponse {
	final boolean success = false
	final List<ErrorMessage> errors
}