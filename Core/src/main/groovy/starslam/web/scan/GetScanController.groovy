package starslam.web.scan

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import starslam.scan.ScanStore

import static groovy.json.JsonOutput.toJson

@Controller
class GetScanController {

    @Autowired ScanStore scanStore

    @RequestMapping(value = "/projects/{projectId}/scans/{scanId}", method = RequestMethod.GET)
    @ResponseBody ResponseEntity<String> get(@PathVariable scanId, @PathVariable projectId) {
        def scanInfo = scanStore.retrieveScan(scanId)
        def headers = new HttpHeaders()
        headers."Content-Type" = "application/json"
        return new ResponseEntity<String>(toJson([
                id:scanInfo.id
                , projectId:scanInfo.projectId
                , rootPath:scanInfo.rootPath
                , status:scanInfo.status.toString()
                , processingTime:scanInfo.processingTime
                , initiatedTime:scanInfo.initiatedTime
                , numberOfFiles:scanStore.filesForScanCount(scanId)
        ]), headers, HttpStatus.OK)
    }
}
