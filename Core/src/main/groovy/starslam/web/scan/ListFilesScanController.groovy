package starslam.web.scan

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import starslam.scan.ScanStore

import static groovy.json.JsonOutput.toJson

@Controller
class ListFilesScanController {

    @Autowired ScanStore scanStore

    @RequestMapping(value = "/projects/{projectId}/scans/{scanId}/files")
    @ResponseBody
    ResponseEntity<String> listFiles(@PathVariable projectId, @PathVariable scanId) {
        def files = scanStore.filesForScan(scanId)
        def headers = new HttpHeaders()
        headers."Content-Type" = 'application/json'
        return new ResponseEntity<String>(toJson(files), headers, HttpStatus.OK)
    }
}
