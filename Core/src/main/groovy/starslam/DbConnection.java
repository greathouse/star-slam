package starslam;

import groovy.sql.Sql;

public interface DbConnection {
	Sql getConnection();
}
