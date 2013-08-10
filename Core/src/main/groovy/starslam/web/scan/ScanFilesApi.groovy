package starslam.web.scan

import org.ratpackframework.handling.Context

import starslam.scan.IScanStore
import starslam.web.RestApiEndpoint

import com.google.inject.Inject

class ScanFilesApi extends RestApiEndpoint {
	private final IScanStore SCAN_STORE
	
	@Inject
	public ScanFilesApi(IScanStore scanStore) {
		SCAN_STORE = scanStore
	}
	
	@Override
	protected void list(Context context) {
		def files = SCAN_STORE.filesForScan(context.pathTokens.scanId)
		sendJson(context, files)
	}

}
