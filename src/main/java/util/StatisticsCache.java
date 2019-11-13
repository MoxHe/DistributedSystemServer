package util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StatisticsCache {

  public static final int CAPACITY = 1000;
  public static long maxPostLatency = 0;
  public static BlockingQueue<Long> postQueue = new LinkedBlockingQueue<>();
  public static long maxGetLatency = 0;
  public static BlockingQueue<Long> getQueue = new LinkedBlockingQueue<>();
}
