package sigprop3.signal.concurrent;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import sigprop3.SinkSignal;
import sigprop3.signal.ClockSignal;

/** A {ClockSignal} that updates the downstream concurrently. */
public final class ConcurrentClockSignal extends ClockSignal {
  private final ScheduledExecutorService executor;

  public ConcurrentClockSignal(
      Supplier<Instant> timeSource,
      Supplier<Duration> nextInterval,
      ScheduledExecutorService executor) {
    super(timeSource, nextInterval, executor);
    this.executor = executor;
  }

  /** Informs all downstream signals to update using the given executor. */
  @Override
  protected final <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    executor.execute(() -> signal.update(timestamp));
  }
}
