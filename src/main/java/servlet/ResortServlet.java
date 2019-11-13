package servlet;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;


public class ResortServlet extends javax.servlet.http.HttpServlet {

   // format1: /resorts
   // format2: /resorts/{resortID}/seasons

  private static final int FORMAT2_LENGTH = 3;
  private static final String FORMAT2 = "seasons";


  protected void doPost(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {

    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    if (urlPath == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isUrlValidForFormat2(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
    } else {
      response.setStatus(HttpServletResponse.SC_OK);

      BufferedReader buf = request.getReader();

      out.write(new Gson().toJson("200"));
    }

  }

  protected void doGet(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {

    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    if (urlPath == null) {
      response.setStatus(HttpServletResponse.SC_OK);
      String jsonString = new Gson().toJson("200");
      out.write(jsonString);
      return;
    }

    String[] urlPaths = urlPath.split("/");

    if (!isUrlValidForFormat1(urlPaths) && !isUrlValidForFormat2(urlPaths)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
      String jsonString = new Gson().toJson("200");
      out.write(jsonString);
    }
  }

  private boolean isUrlValidForFormat1(String[] urlPaths) {
    return urlPaths.length == 0;
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
