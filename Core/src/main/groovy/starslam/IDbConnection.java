package starslam;

import groovy.sql.Sql;

public interface IDbConnection {
	Sql getConnection();
}
