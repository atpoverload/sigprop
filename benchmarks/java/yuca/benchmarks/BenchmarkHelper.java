package yuca.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import yuca.profiler.E2EOperationalCarbonProfiler;
import yuca.profiler.Profiler;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;

final class BenchmarkHelper {
  private static final Logger logger = getLogger();
  private static final int PERIOD =
      Integer.parseInt(System.getProperty("yuca.benchmarks.period", "100"));
  private static final String OUTPUT_DIRECTORY =
      System.getProperty("yuca.benchmarks.output", "/tmp");

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

  static Profiler createProfiler() {
    if (PERIOD < 1) {
      logger.info("Creating E2E profiler");
      return new E2EOperationalCarbonProfiler(SAMPLING_EXECUTOR);
    }
    logger.info(String.format("Creating profiler at %dms", PERIOD));
    return new YucaProfiler(Duration.ofMillis(PERIOD), SAMPLING_EXECUTOR, WORK_EXECUTOR);
  }

  static void dumpProfile(YucaProfile profile, String fileName) {
    String fullFileName = String.format("%s-%s.pb", fileName, UUID.randomUUID());
    try (DataOutputStream out =
        new DataOutputStream(
            new FileOutputStream(Path.of(OUTPUT_DIRECTORY, fullFileName).toString()))) {
      profile.writeTo(out);
    } catch (Exception e) {
    }
  }

  private BenchmarkHelper() {}
}
