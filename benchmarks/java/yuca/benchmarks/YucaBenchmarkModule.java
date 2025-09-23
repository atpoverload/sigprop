package yuca.benchmarks;

import static charcoal.util.ConcurrencyUtil.newDaemonExecutor;
import static charcoal.util.LoggerUtil.getLogger;
import static yuca.profiler.emissions.CarbonLocale.DEFAULT_LOCALE;
import static yuca.profiler.emissions.EmbodiedCarbon.SYSTEM_EMBODIED_CARBON;
import static yuca.profiler.linux.freq.CpuFreq.MEDIAN_FREQUENCY;

import charcoal.util.Timestamps;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;
import yuca.profiler.util.YucaProfilerFactory;

final class YucaBenchmarkModule {
  private static final Logger logger = getLogger("yuca-benchmark");

  private static final ScheduledExecutorService SAMPLING_EXECUTOR =
      newDaemonExecutor("yuca-sampling-thread");
  private static final ScheduledExecutorService PROCESSING_EXECUTOR =
      newDaemonExecutor("yuca-processing-thread");
  private static final YucaProfilerFactory PROFILER_FACTORY =
      new YucaProfilerFactory(
          Timestamps::now,
          DEFAULT_LOCALE,
          SYSTEM_EMBODIED_CARBON,
          MEDIAN_FREQUENCY,
          SAMPLING_EXECUTOR,
          PROCESSING_EXECUTOR);

  static YucaProfiler createProfiler() {
    try {
      ProfilerKind kind = ProfilerKind.valueOf(System.getProperty("yuca.benchmarks.profiler"));
      switch (kind) {
        case END_TO_END:
          logger.info("creating end-to-end profiler");
          return PROFILER_FACTORY.createEndToEnd();
        case ONLINE:
          break;
      }
    } catch (Exception e) {
    }
    Duration period = getPeriod();
    logger.info(String.format("creating online profiler at %s", period));
    return PROFILER_FACTORY.createFixedPeriod(period);
  }

  static void writeProfile(YucaProfile profile, String fileName) {
    Path filePath = Path.of(getOutputDirectory(), String.format("%s.pb", fileName));
    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
      logger.info(String.format("writing profile to %s", filePath));
      profile.writeTo(out);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "unable to write profile", e);
    }
  }

  private static String getOutputDirectory() {
    return System.getProperty("yuca.benchmarks.output", "/tmp");
  }

  private enum ProfilerKind {
    END_TO_END,
    ONLINE;
  }

  private static Duration getPeriod() {
    try {
      return Duration.ofMillis(
          Long.parseLong(System.getProperty("yuca.benchmarks.profiler.period")));
    } catch (NumberFormatException e) {
      return Duration.ofMillis(100);
    }
  }

  private YucaBenchmarkModule() {}
}
