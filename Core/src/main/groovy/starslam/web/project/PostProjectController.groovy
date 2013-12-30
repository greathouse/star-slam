package starslam.web.project

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import starslam.project.DuplicateProjectNameException
import starslam.project.Project
import starslam.project.ProjectStore
import starslam.web.ErrorMessage
import starslam.web.PostFailureResponse
import starslam.web.PostSuccessResponse

import static groovy.json.JsonOutput.toJson
import static starslam.web.Validation.REQUIRED

@Controller
class PostProjectController {

    @Autowired protected ProjectStore projectStore

    @RequestMapping(value="/projects", method = RequestMethod.POST)
    public ResponseEntity<String> post(@RequestBody String body) {
        def json = new JsonSlurper().parseText(body)
        def errors = performValidation(json)

        def headers = new HttpHeaders()
        if (errors) {
            return new ResponseEntity<String>(toJson(new PostFailureResponse(errors: errors)), headers, HttpStatus.BAD_REQUEST)
        }

        def project = new Project(fileGlob: json.fileGlob, name: json.name, rootPath: json.rootPath)
        try {
            def id = projectStore.persist(project)
            headers."Location" = "/projects/${id}".toString()
            return new ResponseEntity<String>(toJson(new PostSuccessResponse(id: id)), headers, HttpStatus.OK)
        }
        catch (DuplicateProjectNameException e) {
            return new ResponseEntity<String>(toJson(new PostFailureResponse(errors: [
                    new ErrorMessage(
                            code: "DUPLICATE_PROJECT_NAME"
                            , message: "A project named ${json.name} already exists. Please choose a different name."
                            , property: "name"
                    )
            ])), headers, HttpStatus.BAD_REQUEST)
        }
    }

    private ArrayList performValidation(json) {
        def errors = []
        if (!json.name) {
            errors << [code: REQUIRED, property: 'name', message: 'Project name is required']
        }
        if (!json.rootPath) {
            errors << [code: REQUIRED, property: 'rootPath', message: 'Root Path is required']
        }
        if (!json.fileGlob) {
            errors << [code: REQUIRED, property: 'fileGlob', message: 'File Glob is required']
        }
        errors
    }
}
