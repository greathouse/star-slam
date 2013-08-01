package starslam


import com.google.inject.AbstractModule
import com.google.inject.name.Names

class DefaultModule extends AbstractModule {
	final String DBURL
	
	public DefaultModule(String dbUrl) {
		DBURL = dbUrl
	}

	@Override
	protected void configure() {
		bind(String).annotatedWith(Names.named("JDBC_URL")).toInstance(DBURL)
		bind(IDbConnection).to(DatabaseConnector)
	}

}
