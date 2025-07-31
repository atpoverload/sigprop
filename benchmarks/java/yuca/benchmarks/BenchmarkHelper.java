package yuca.benchmarks;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;

final class BenchmarkHelper {
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

  static YucaProfiler createProfiler(int periodMillis) {
    return new YucaProfiler(Duration.ofMillis(periodMillis), SAMPLING_EXECUTOR, WORK_EXECUTOR);
  }

  static String createTempOutputPath(String suite, String benchmark, int iteration) {
    return String.format("/tmp/%s-%s-%d.pb", suite, benchmark, iteration);
  }

  static void dumpProfile(YucaProfile profile, String outputPath) {
    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outputPath))) {
      profile.writeTo(out);
    } catch (Exception e) {
    }
  }

  private BenchmarkHelper() {}
}
