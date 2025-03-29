package yuca.benchmarks;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;

public final class ProfilerUtil {
  private static final String OUTPUT =
      System.getProperty(
          "yuca.benchmarks.output",
          String.format("/tmp/dacapo-%d-report.pb", ProcessHandle.current().pid()));

  private static final ScheduledExecutorService SAMPLING_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "yuca-sampling-thread");
            t.setDaemon(true);
            return t;
          });

  private static final ScheduledExecutorService WORK_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "yuca-worker-thread");
            t.setDaemon(true);
            return t;
          });

  public static YucaProfiler newProfiler() {
    return new YucaProfiler(Duration.ofMillis(10), SAMPLING_EXECUTOR, WORK_EXECUTOR);
  }

  public static void dumpProfile(YucaProfile profile) {
    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(OUTPUT))) {
      profile.writeTo(out);
    } catch (Exception e) {

    }
  }

  private ProfilerUtil() {}
}
