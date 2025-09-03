package charcoal.util;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/** Utility to create daemon threads, which are easier to manage for side-sampling. */
public final class ConcurrencyUtil {
  /** Creates a daemon thread from a runnable and thread name. */
  public static Thread newDaemonThread(Runnable r, String threadName) {
    Thread t = new Thread(r, threadName);
    t.setDaemon(true);
    return t;
  }

  /** Creates a threadpool that creates daemon threads. */
  public static ThreadFactory newDaemonThreadFactory(String threadName) {
    return r -> newDaemonThread(r, threadName);
  }

  /** Create an executor of daemon threads. */
  public static ScheduledExecutorService newDaemonExecutor(String threadName) {
    return newSingleThreadScheduledExecutor(newDaemonThreadFactory(threadName));
  }

  private ConcurrencyUtil() {}
}
