package starslam.web.scan

import org.ratpackframework.handling.Context

import starslam.scan.IScanService
import starslam.scan.IScanStore
import starslam.web.RestApiEndpoint

import com.google.inject.Inject

class ScanApi extends RestApiEndpoint {
	private final IScanService SCAN_SERVICE
	private final IScanStore SCAN_STORE
	
	@Inject
	public ScanApi(IScanService scanService, IScanStore scanStore) {
		SCAN_SERVICE = scanService
		SCAN_STORE = scanStore
	}
	
	@Override
	protected void get(Context context, String id) {
		def scanInfo = SCAN_STORE.retrieveScan(id)
		sendJson(context, [
			rootPath:scanInfo.rootPath
			, status:scanInfo.status
			, processingTime:scanInfo.processingTime
			, initiatedTime:scanInfo.initiatedTime
			, numberOfFiles:SCAN_STORE.filesForScanCount(id)
		])
	}

	@Override
	protected void post(Context context) {
		def scanInfo = SCAN_SERVICE.initiate(context.pathTokens.projectId, {  }, {  }) {}
		header(context, "Location", "/projects/${scanInfo.projectId}/scans/${scanInfo.id}")
		sendJson(context, scanInfo)
	}

	@Override
	protected void list(Context context) {
		def scans = SCAN_STORE.scansForProject(context.pathTokens.projectId)
		sendJson(context, scans)
	}
}
