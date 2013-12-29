package starslam

import groovy.sql.Sql

class OpenDatabase {
	def static getConnection(DbConnection conn, Closure closure) {
		Sql sql
		try {
			sql = conn.getConnection()
			
			if (closure) {
				closure(sql)
			}
		}
		finally {
			sql.close()
		}
	}
}
