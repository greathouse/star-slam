package starslam.scan

import com.google.inject.AbstractModule

class ScanModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IScanService).to(ScanService)
	}

}
