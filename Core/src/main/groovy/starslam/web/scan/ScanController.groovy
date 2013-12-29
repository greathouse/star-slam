package starslam.web.scan

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import starslam.scan.ScanService
import starslam.scan.ScanStore

import static groovy.json.JsonOutput.toJson

@Controller
class ScanController {
	@Autowired ScanService scanService
	@Autowired ScanStore scanStore

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
						id:scanInfo.id
						, projectId:scanInfo.projectId
		        , rootPath:scanInfo.rootPath
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
