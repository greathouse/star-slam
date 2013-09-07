package starslam

import groovy.sql.Sql

class DatabaseConnector implements IDbConnection {
	private final String DBURL;
	
	public DatabaseConnector(String dbUrl) {
		DBURL = dbUrl
	}
	
	@Override
	public Sql getConnection() {
		return Sql.newInstance(DBURL, '', '', 'org.h2.Driver')
	}
	
}
