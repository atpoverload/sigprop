package yuca.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import yuca.profiler.E2EOperationalCarbonProfiler;
import yuca.profiler.Profiler;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;

final class BenchmarkHelper {
  private static final Logger logger = getLogger();

  private enum ProfilerKind {
    YUCA,
    END2END;

    private Profiler createProfiler() {
      switch (this) {
        case YUCA:
          logger.info("Creating E2E profiler");
          return new E2EOperationalCarbonProfiler(SAMPLING_EXECUTOR);
        case END2END:
          logger.info(String.format("Creating profiler at %dms", PERIOD));
          return new YucaProfiler(Duration.ofMillis(PERIOD), SAMPLING_EXECUTOR, WORK_EXECUTOR);
        default:
          throw new IllegalStateException("how did we get here?");
      }
    }

    private static ProfilerKind getProfilerKind() {
      String kindValue = System.getProperty("yuca.benchmarks.profiler", "yuca");
      logger.info(String.format("checking for profiler %s", kindValue));
      try {
        ProfilerKind kind = ProfilerKind.valueOf(kindValue.toUpperCase(Locale.getDefault()));
        logger.info(String.format("using %s profiler", kind));
        return kind;
      } catch (IllegalArgumentException e) {
        logger.info(String.format("no profiler founds for %s", kindValue));
        logger.info(String.format("options are %s", Arrays.toString(ProfilerKind.values())));
        logger.info(String.format("falling back to period check"));
        if (PERIOD_MS < 1) {
          logger.info(String.format("using %s profiler", ProfilerKind.END2END));
          return ProfilerKind.END2END;
        }
        logger.info(String.format("using %s profiler", ProfilerKind.YUCA));
        return ProfilerKind.YUCA;
      }
    }
  }

  private static final int PERIOD_MS = getPeriodMillis();
  private static final ProfilerKind PROFILER_KIND = ProfilerKind.getProfilerKind();
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
    return PROFILER_KIND.createProfiler();
  }

  static void writeProfile(YucaProfile profile, String fileName) {
    String fullFileName = String.format("%s_%s.pb", fileName, UUID.randomUUID());
    logger.info(String.format("writing %s to %s", fullFileName, OUTPUT_DIRECTORY));
    try (DataOutputStream out =
        new DataOutputStream(
            new FileOutputStream(Path.of(OUTPUT_DIRECTORY, fullFileName).toString()))) {
      profile.writeTo(out);
    } catch (Exception e) {
      logger.log(
          Level.SEVERE,
          String.format("couldn't write %s to %s", fullFileName, OUTPUT_DIRECTORY),
          e);
    }
    logger.info(String.format("wrote %s to %s", fullFileName, OUTPUT_DIRECTORY));
  }

  private static int getPeriodMillis() {
    logger.info("checking for period in ms");
    String periodValue = System.getProperty("yuca.benchmarks.period", "100");
    try {
      int period = Integer.parseInt(periodValue);
    } catch (Exception e) {
      logger.info(String.format("got bad period %s", periodValue));
      logger.info("falling back to default 100ms");
      return 100;
    }
  }

  private BenchmarkHelper() {}
}
