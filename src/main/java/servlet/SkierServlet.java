package servlet;

import db.DBCPDataSource;
import javax.sql.DataSource;
import util.StatisticsCache;
import util.StatisticsCache;

import com.google.gson.Gson;
import db.LiftRideDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class SkierServlet extends javax.servlet.http.HttpServlet {

  // format1
  // /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
  private static final int FORMAT1_LENGTH = 8;
  private static final String[] FORMAT1 = {"seasons", "days", "skiers"};

  // format2
  // /skiers/{skierID}/vertical
  private static final int FORMAT2_LENGTH = 3;
  private static final String FORMAT2 = "vertical";

  private static final int DAY_START = 1;
  private static final int DAY_END = 366;

  private static final int VERTICAL_FACTOR = 10;

  private static DataSource pool = DBCPDataSource.createPool();
  protected void doPost(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {

    long startTime = System.currentTimeMillis();

    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    if (urlPath == null || urlPath.length() == 0) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isUrlValidForFormat1(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
    } else {

      StringBuilder buffer = new StringBuilder();
      BufferedReader reader = request.getReader();
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
      reader.close();

      String body = buffer.toString();
      String[] parsedBody = parseBody(body);

      int resortID = Integer.parseInt(urlParts[1]);
      String seasonID = urlParts[3];
      String dayID = urlParts[5];
      int skierID = Integer.parseInt(urlParts[7]);
      int liftID = Integer.parseInt(parsedBody[0]);
      String time = parsedBody[1];
      int vertical = VERTICAL_FACTOR * liftID;

      LiftRideDao liftRideDao = new LiftRideDao(pool);
      liftRideDao.insertIntoLiftRides(resortID, seasonID, dayID, skierID, liftID, time, vertical);
      liftRideDao.updateTotalVertical(resortID, seasonID, dayID, skierID, vertical);
      liftRideDao.closeConnection();

      response.setStatus(HttpServletResponse.SC_OK);
      out.write(new Gson().toJson(200));

      long latency = System.currentTimeMillis() - startTime;
      if (latency > StatisticsCache.maxPostLatency) {
        StatisticsCache.maxPostLatency = latency;
      }
      StatisticsCache.postQueue.offer(latency);
      if (StatisticsCache.postQueue.size() > StatisticsCache.CAPACITY) {
        StatisticsCache.postQueue.poll();
      }
    }
  }

  protected void doGet(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {

    long startTime = System.currentTimeMillis();

    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    if (urlPath == null || urlPath.length() == 0) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (isUrlValidForFormat1(urlParts)) {
      response.setStatus(HttpServletResponse.SC_OK);

      int resortID = Integer.parseInt(urlParts[1]);
      String seasonID = urlParts[3];
      String dayID = urlParts[5];
      int skierID = Integer.parseInt(urlParts[7]);

      LiftRideDao liftRideDao = new LiftRideDao(pool);
      int totalVertical = liftRideDao.getTotalVertical(resortID, seasonID, dayID, skierID);
      liftRideDao.closeConnection();

      out.write(new Gson().toJson(totalVertical));

    } else if (isUrlValidForFormat2(urlParts)) {
      response.setStatus(HttpServletResponse.SC_OK);
      String jsonString = new Gson().toJson(2);
      out.write(jsonString);

    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
    }

    long latency = System.currentTimeMillis() - startTime;
    if (latency > StatisticsCache.maxGetLatency) {
      StatisticsCache.maxGetLatency = latency;
    }
    StatisticsCache.getQueue.offer(latency);
    if (StatisticsCache.getQueue.size() > StatisticsCache.CAPACITY) {
      StatisticsCache.getQueue.poll();
    }
  }

  private String[] parseBody(String body) {
    String[] parts = body.split(",");
    String timePair = parts[0], liftPair = parts[1];
    String temp = liftPair.split(":")[1];

    String lift = temp.substring(0, temp.length() - 1);
    String time = timePair.split(":")[1];

    return new String[] {lift, time};
  }

  private boolean isUrlValidForFormat1(String[] urlPaths) {

    if (urlPaths.length != FORMAT1_LENGTH) {
      return false;
    }

    for (int i = 1; i < urlPaths.length; i += 2) {
      try {
        Integer.parseInt(urlPaths[i]);
      } catch (NumberFormatException e) {
        return false;
      }
    }

    if (Integer.parseInt(urlPaths[5]) < DAY_START ||
        Integer.parseInt(urlPaths[5]) > DAY_END) {
      return false;
    }

    List<String> formats = new ArrayList<>();
    for (int i = 2; i < urlPaths.length; i += 2) {
      formats.add(urlPaths[i]);
    }

    for (int i = 0; i < formats.size(); i++) {
      if (!formats.get(i).equals(FORMAT1[i])) {
        return false;
      }
    }
    return true;
  }

  private boolean isUrlValidForFormat2(String[] urlPaths) {
    if (urlPaths.length != FORMAT2_LENGTH) {
      return false;
    }
    try {
      Integer.parseInt(urlPaths[1]);
    } catch (NumberFormatException e) {
      return false;
    }

    if (!urlPaths[2].equals(FORMAT2)) {
      return false;
    }

    return true;
  }
}
