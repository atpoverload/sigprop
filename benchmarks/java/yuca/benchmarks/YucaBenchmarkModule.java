package yuca.benchmarks;

import static charcoal.util.ConcurrencyUtil.newDaemonExecutor;
import static yuca.profiler.emissions.CarbonLocale.DEFAULT_LOCALE;
import static yuca.profiler.emissions.EmbodiedCarbon.SYSTEM_EMBODIED_CARBON;
import static yuca.profiler.linux.freq.CpuFreq.MEDIAN_FREQUENCY;

import charcoal.util.Timestamps;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;
import yuca.profiler.util.YucaProfilerFactory;

final class YucaBenchmarkModule {
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
      ProfilerKind kind = ProfilerKind.valueOf(System.getProperty("yuca.benchmarks.profiler.kind"));
      switch (kind) {
        case ENDTOEND:
          return PROFILER_FACTORY.createEndToEnd();
        case ONLINE:
          return PROFILER_FACTORY.createFixedPeriod(getPeriod());
      }
    } catch (Exception e) {
    }
    return PROFILER_FACTORY.createFixedPeriod(getPeriod());
  }

  static void writeProfile(YucaProfile profile, String fileName) {
    try (DataOutputStream out =
        new DataOutputStream(new FileOutputStream(Path.of(fileName).toString()))) {
      profile.writeTo(out);
    } catch (Exception e) {
    }
  }

  private enum ProfilerKind {
    ENDTOEND,
    ONLINE;
  }

  private static Duration getPeriod() {
    try {
      return Duration.ofMillis(
          Long.parseLong(System.getProperty("yuca.benchmarks.profiler.periodms")));
    } catch (NumberFormatException e) {
      return Duration.ofMillis(100);
    }
  }

  private YucaBenchmarkModule() {}
}
