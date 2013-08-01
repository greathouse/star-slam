package starslam

import groovy.sql.Sql

import com.google.inject.Inject
import com.google.inject.name.Named

class DatabaseConnector implements IDbConnection {
	private final String DBURL;
	
	@Inject
	public DatabaseConnector(@Named("JDBC_URL") String dbUrl) {
		DBURL = dbUrl
	}
	
	@Override
	public Sql getConnection() {
		return Sql.newInstance(DBURL, '', '', 'org.h2.Driver')
	}
	
}
