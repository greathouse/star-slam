package starslam.web.scan

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import starslam.scan.IScanService
import starslam.scan.IScanStore
import starslam.web.PostSuccessResponse

import static groovy.json.JsonOutput.toJson

@Controller
class ScanController {
	@Autowired IScanService scanService
	@Autowired IScanStore scanStore

	@RequestMapping(value = "/projects/{projectId}/scans", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<String> post(@RequestBody String body, @PathVariable projectId) {
		println "Initiated Scan"
		def scanInfo = scanService.initiate(projectId, {},{},{})
		def headers = new HttpHeaders()
		headers."Location" = "/projects/${projectId}/scans/${scanInfo.id}".toString()
		return new ResponseEntity<String>(toJson(scanInfo), headers, HttpStatus.OK)
	}

	@RequestMapping(value = "/projects/{projectId}/scans/{scanId}", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<String> get(@PathVariable scanId, @PathVariable projectId) {
		def scanInfo = scanStore.retrieveScan(scanId)
		def headers = new HttpHeaders()
		headers."Content-Type" = "application/json"
		return new ResponseEntity<String>(toJson([
		        rootPath:scanInfo.rootPath
						, status:scanInfo.status.toString()
						, processingTime:scanInfo.processingTime
						, initiatedTime:scanInfo.initiatedTime
						, numberOfFiles:scanStore.filesForScanCount(scanId)
		]), headers, HttpStatus.OK)
	}

	@RequestMapping(value = "/projects/{projectId}/scans", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<String> list(@PathVariable projectId) {
		def scans = scanStore.scansForProject(projectId)
		def headers = new HttpHeaders()
		headers."Content-Type" = "application/json"
		return new ResponseEntity<String>(toJson(scans), headers, HttpStatus.OK)
	}

	@RequestMapping(value = "/projects/{projectId}/scans/{scanId}/files")
	@ResponseBody ResponseEntity<String> listFiles(@PathVariable projectId, @PathVariable scanId) {
		def files = scanStore.filesForScan(scanId)
		def headers = new HttpHeaders()
		headers."Content-Type" = 'application/json'
		return new ResponseEntity<String>(toJson(files), headers, HttpStatus.OK)
	}
}
