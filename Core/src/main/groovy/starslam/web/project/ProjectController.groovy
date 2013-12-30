package starslam.web.project

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import starslam.project.DuplicateProjectNameException
import starslam.project.ProjectStore
import starslam.project.Project
import starslam.web.ErrorMessage
import starslam.web.PostFailureResponse
import starslam.web.PostSuccessResponse
import static starslam.web.Validation.*

import static groovy.json.JsonOutput.toJson

@RequestMapping("/projects")
abstract class ProjectController {
	@Autowired protected ProjectStore projectStore
}
