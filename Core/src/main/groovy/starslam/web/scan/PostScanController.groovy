package starslam.web.scan

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
import starslam.scan.ScanService

import static groovy.json.JsonOutput.toJson

@Controller
class PostScanController {

    @Autowired ScanService scanService

    @RequestMapping(value = "/projects/{projectId}/scans", method = RequestMethod.POST)
    @ResponseBody ResponseEntity<String> post(@RequestBody String body, @PathVariable projectId) {
        println "Initiated Scan"
        def scanInfo = scanService.initiate(projectId, {},{},{})
        def headers = new HttpHeaders()
        headers."Location" = "/projects/${projectId}/scans/${scanInfo.id}".toString()
        return new ResponseEntity<String>(toJson(scanInfo), headers, HttpStatus.OK)
    }

}
