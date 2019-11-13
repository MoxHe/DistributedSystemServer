package db;
import java.sql.*;

public class LiftRideDao {
  private Connection conn;
  private PreparedStatement ps;

  public LiftRideDao() {
    try {
      conn = DBCPDataSource.getConnection();
      ps = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void createLiftRides() {
    String sql = null;
    try {
      sql = "DROP TABLE IF EXISTS LiftRidesWrite";
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();

      sql = "CREATE TABLE LiftRidesWrite("
          + "resortID INT NOT NULL,"
          + "seasonID VARCHAR(255) NOT NULL,"
          + "dayID VARCHAR(255) NOT NULL,"
          + "skierID INT NOT NULL,"
          + "liftID INT NOT NULL,"
          + "time VARCHAR(255) NOT NULL,"
          + "vertical INT NOT NULL,"
          + "PRIMARY KEY(resortID, seasonID, dayID, skierID, time))";
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }

    try {
      sql = "DROP TABLE IF EXISTS LiftRidesRead";
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();

      sql = "CREATE TABLE LiftRidesRead("
          + "resortID INT NOT NULL,"
          + "seasonID VARCHAR(255) NOT NULL,"
          + "dayID VARCHAR(255) NOT NULL,"
          + "skierID INT NOT NULL,"
          + "totalVertical INT NOT NULL,"
          + "PRIMARY KEY(resortID, seasonID, dayID, skierID))";
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void deleteLiftRides() {
    String sql = "DROP TABLE IF EXISTS LiftRidesWrite";
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    sql = "DROP TABLE IF EXISTS LiftRidesRead";
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void closeConnection() {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void insertIntoLiftRides(int resortID, String seasonID, String dayID, int skierID,
      int liftID, String time, int vertical) {

    String sql = "INSERT IGNORE INTO LiftRidesWrite (resortID, seasonID, dayID, skierID, liftID, time, vertical) " +
        "VALUES (?,?,?,?,?,?,?)";

    try {
      ps = conn.prepareStatement(sql);
      ps.setInt(1, resortID);
      ps.setString(2, seasonID);
      ps.setString(3, dayID);
      ps.setInt(4, skierID);
      ps.setInt(5, liftID);
      ps.setString(6, time);
      ps.setInt(7, vertical);

      // execute insert SQL statement
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int getTotalVertical(int resortID, String seasonID, String dayID, int skierID) {
    String sql = "SELECT * FROM LiftRidesRead WHERE resortID = ? AND seasonID = ? AND dayID = ? AND skierID = ?";
    try {
      ps = conn.prepareStatement(sql);
      ps.setInt(1, resortID);
      ps.setString(2, seasonID);
      ps.setString(3, dayID);
      ps.setInt(4, skierID);

      ResultSet rs = ps.executeQuery();
      rs.next();
      return rs.getInt("totalVertical");

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Integer.MIN_VALUE;
  }

  public void updateTotalVertical(int resortID, String seasonID, String dayID, int skierID, int vertical) {
    String sql = "INSERT INTO LiftRidesRead (resortID, seasonID, dayID, skierID, totalVertical) values(?, ?, ?, ?, ?) "+
        "ON DUPLICATE KEY UPDATE totalVertical = totalVertical + VALUES(totalVertical)";

    try {
      ps = conn.prepareStatement(sql);
      ps.setInt(1, resortID);
      ps.setString(2, seasonID);
      ps.setString(3, dayID);
      ps.setInt(4, skierID);
      ps.setInt(5, vertical);
      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    LiftRideDao liftRideDao = new LiftRideDao();
    liftRideDao.createLiftRides();
  }

}