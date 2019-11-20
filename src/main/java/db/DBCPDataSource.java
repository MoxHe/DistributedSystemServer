package db;

import java.sql.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.*;

import javax.sql.DataSource;
import javax.xml.crypto.Data;


public class DBCPDataSource {

  // NEVER store sensitive information below in plain text!
//    private static final String HOST_NAME = "db-cs6650.cmz8olexymfs.us-west-2.rds.amazonaws.com";
//    private static final String PORT = "3306";
//    private static final String DATABASE = "A2_DB";
//    private static final String USERNAME = "admin";
//    private static final String PASSWORD = "wdra233666";
//      private static final String HOST_NAME = System.getProperty("MySQL_IP_ADDRESS");
//      private static final String PORT = System.getProperty("MySQL_PORT");
//      private static final String DATABASE = "A2_DB";
//      private static final String USERNAME = System.getProperty("DB_USERNAME");
//      private static final String PASSWORD = System.getProperty("DB_PASSWORD");
  private static final String DB_NAME = System.getProperty("DB_NAME");
  private static final String DB_USER = System.getProperty("DB_USER");
  private static final String DB_PASS = System.getProperty("DB_PASS");
  private static final String CLOUD_SQL_CONNECTION_NAME = System
      .getProperty("CLOUD_SQL_CONNECTION_NAME");
  //      private static final String CLOUD_SQL_CONNECTION_NAME = "firstproject-259305:us-west2:firstcloudsql";
  private static DataSource pool;

  public static DataSource createPool() {
    HikariConfig config = new HikariConfig();
// Configure which instance and what database user to connect with.
    config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
    config.setUsername(DB_USER); // e.g. "root", "postgres"
    config.setPassword(DB_PASS); // e.g. "my-password"
// For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
    // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory
    config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
    config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
    config.addDataSourceProperty("useSSL", "false");

    config.setMaximumPoolSize(100);
    // minimumIdle is the minimum number of idle connections Hikari maintains in the pool.
    // Additional connections will be established to meet this value unless the pool is full.
    config.setMinimumIdle(10);
    // [END cloud_sql_mysql_servlet_limit]

    // [START cloud_sql_mysql_servlet_timeout]
    // setConnectionTimeout is the maximum number of milliseconds to wait for a connection checkout.
    // Any attempt to retrieve a connection from this pool that exceeds the set limit will throw an
    // SQLException.
    config.setConnectionTimeout(60000); // 1 min
    // idleTimeout is the maximum amount of time a connection can sit in the pool. Connections that
    // sit idle for this many milliseconds are retried if minimumIdle is exceeded.
    config.setIdleTimeout(600000); // 10 minutes
    // [END cloud_sql_mysql_servlet_timeout]

    // [START cloud_sql_mysql_servlet_backoff]
    // Hikari automatically delays between failed connection attempts, eventually reaching a
    // maximum delay of `connectionTimeout / 2` between attempts.
    // [END cloud_sql_mysql_servlet_backoff]

    // [START cloud_sql_mysql_servlet_lifetime]
    // maxLifetime is the maximum possible lifetime of a connection in the pool. Connections that
    // live longer than this many milliseconds will be closed and reestablished between uses. This
    // value should be several minutes shorter than the database's timeout value to avoid unexpected
    // terminations.
    config.setMaxLifetime(1800000); // 30 minutes
    pool = new HikariDataSource(config);
    return pool;
  }
}