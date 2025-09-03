package yuca.profiler.util;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import yuca.profiler.EndToEndProfiler;
import yuca.profiler.OnlineYucaProfiler;
import yuca.profiler.emissions.CarbonLocale;

/** Utility to create new profilers easily. */
public final class YucaProfilerFactory {
  private final Supplier<Instant> timeSource;
  private final CarbonLocale locale;
  private final double systemEmbodiedCarbon;
  private final long normalFrequency;
  private final ScheduledExecutorService samplingExecutor;
  private final ScheduledExecutorService processingExecutor;

  public YucaProfilerFactory(
      Supplier<Instant> timeSource,
      CarbonLocale locale,
      double systemEmbodiedCarbon,
      long normalFrequency,
      ScheduledExecutorService samplingExecutor,
      ScheduledExecutorService processingExecutor) {
    this.timeSource = timeSource;
    this.locale = locale;
    this.systemEmbodiedCarbon = systemEmbodiedCarbon;
    this.normalFrequency = normalFrequency;
    this.samplingExecutor = samplingExecutor;
    this.processingExecutor = processingExecutor;
  }

  public EndToEndProfiler createEndToEnd() {
    return new EndToEndProfiler(timeSource, locale, samplingExecutor);
  }

  public OnlineYucaProfiler fixedPeriod(Duration period) {
    return new OnlineYucaProfiler(
        timeSource,
        () -> period,
        locale,
        systemEmbodiedCarbon,
        normalFrequency,
        samplingExecutor,
        processingExecutor);
  }
}
