package db;

import java.sql.*;
import org.apache.commons.dbcp2.*;

public class DBCPDataSource {
  private static BasicDataSource dataSource = new BasicDataSource();

//  private static final String HOST_NAME = "localhost";
  private static final String HOST_NAME = "database-1.cufeptd00xps.us-east-1.rds.amazonaws.com";
  private static final String PORT = "3306";
  private static final String DATABASE = "LiftRides";
//  private static final String USERNAME = "root";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "Hmx_19950228";

  static {
    String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
    dataSource.setUrl(url);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setMinIdle(0);
    dataSource.setMaxIdle(-1);
    dataSource.setMaxTotal(-1);
    dataSource.setMaxOpenPreparedStatements(-1);
  }

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
