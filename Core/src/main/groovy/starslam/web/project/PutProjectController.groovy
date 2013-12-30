package starslam.web.project

import groovy.json.JsonSlurper
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import starslam.project.Project
import starslam.web.PostSuccessResponse

import static groovy.json.JsonOutput.toJson

@Controller
class PutProjectController extends ProjectController {
    @RequestMapping(value="{projectId}", method = RequestMethod.PUT)
    @ResponseBody ResponseEntity<String> put(@PathVariable projectId, @RequestBody String body) {
        def json = new JsonSlurper().parseText(body)
        def project = new Project(id: projectId, fileGlob: json.fileGlob, name: json.name, rootPath: json.rootPath)
        projectStore.persist(project)
        return new ResponseEntity<String>(toJson(new PostSuccessResponse(id: projectId)), new HttpHeaders(), HttpStatus.OK)
    }

}
