package servlet;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.StatisticsCache;

@WebServlet(name = "StatisticsServlet")
public class StatisticsServlet extends HttpServlet {

  protected void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
  }

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    if (urlPath == null || urlPath.split("/").length == 0) {

      long getSum = 0;
      for (long latency: StatisticsCache.getQueue) {
        getSum += latency;
      }

      long meanGetLatency = 0;
      if (StatisticsCache.getQueue.size() != 0) {
        meanGetLatency = getSum / StatisticsCache.getQueue.size();
      }
      long postSum = 0;
      for (long latency: StatisticsCache.postQueue) {
        postSum += latency;
      }
      long meanPostLatency = 0;
      if (StatisticsCache.postQueue.size() != 0) {
        meanPostLatency = postSum / StatisticsCache.postQueue.size();
      }

      String statement = "The mean get latency: " + meanGetLatency + ", "
                      + "The max get latency: " + StatisticsCache.maxGetLatency + ", "
                      + "The mean post latency: " + meanPostLatency + ", "
                      + "The max post latency: " + StatisticsCache.maxPostLatency;
      out.write(new Gson().toJson(statement));
    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("INVALID URL");
      out.write(jsonString);
    }

  }
}
