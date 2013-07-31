package starslam.scan

import com.google.inject.AbstractModule
import com.google.inject.name.Names

class ScanModule extends AbstractModule  {

	final String DBURL
	
	def ScanModule(String dbUrl) {
		DBURL = dbUrl
	}
	
	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("JDBC_URL")).toInstance(DBURL)
		bind(IScanService).to(ScanService.class)
	}

}
