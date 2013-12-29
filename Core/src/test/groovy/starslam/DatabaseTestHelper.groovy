package starslam

import groovy.sql.Sql
import org.springframework.context.ApplicationContext

class DatabaseTestHelper {
    private static porpoised = false;
    private static Sql sql

    public static void setup(ApplicationContext context) {
        if (!porpoised) {
            new Bootstrapper().porpoise(context.getBean("dbUrl"))
            porpoised = true
        }

        sql = context.getBean(DbConnection).connection
        cleanUpDatabase()
    }

    private static void cleanUpDatabase() {
        [
                'ScannedFile'
                ,'Scan'
                , 'Project'
        ].each {
            sql.execute("delete from ${it}".toString())
        }
    }

    public static Sql sql() {
        sql
    }
}
