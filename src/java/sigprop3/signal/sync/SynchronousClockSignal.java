package sigprop3.signal.sync;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import sigprop3.SinkSignal;
import sigprop3.signal.ClockSignal;

/** A {@link ClockSignal} that updates the downstream synchronously. */
public final class SynchronousClockSignal extends ClockSignal {
  public static SynchronousClockSignal fixedPeriodMillis(
      int millisPeriod, ScheduledExecutorService executorService) {
    final Duration period = Duration.ofMillis(millisPeriod);
    return new SynchronousClockSignal(Instant::now, () -> period, executorService);
  }

  public SynchronousClockSignal(
      Supplier<Instant> timeSource,
      Supplier<Duration> nextInterval,
      ScheduledExecutorService executor) {
    super(timeSource, nextInterval, executor);
  }

  /** Directly calls update for the signal. */
  @Override
  protected <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    signal.update(timestamp);
  }
}
