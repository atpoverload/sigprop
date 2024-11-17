package sigprop2.signal.concurrent;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import sigprop2.signal.Clock;

/** A {@link ConcurrentSubscribeableSignal} that updates the downstream with the current time. */
public final class ConcurrentClock extends ConcurrentSubscribeableSignal<Instant> implements Clock {
  /** Start a {@link ConcurrentClock} that samples at a fixed {@link Duration}. */
  public static ConcurrentClock fixedPeriod(Duration period, ScheduledExecutorService executor) {
    return fromPeriodSupplier(() -> period, executor);
  }

  /** Start a {@link ConcurrentClock} that samples at a fixed millisecond period. */
  public static ConcurrentClock fixedPeriodMillis(
      int periodMillis, ScheduledExecutorService executor) {
    Duration period = Duration.ofMillis(periodMillis);
    return fixedPeriod(period, executor);
  }

  /** Start a {@link ConcurrentClock} that samples using a {@link Supplier} of {@link Durations}. */
  public static ConcurrentClock fromPeriodSupplier(
      Supplier<Duration> periodSupplier, ScheduledExecutorService executor) {
    return new ConcurrentClock(Instant::now, periodSupplier, executor);
  }

  /** Start a {@link ConcurrentClock} that samples using an {@link IntSupplier} of milliseconds. */
  public static ConcurrentClock fromMillisSupplier(
      IntSupplier periodMillisSupplier, ScheduledExecutorService executor) {
    return fromPeriodSupplier(() -> Duration.ofMillis(periodMillisSupplier.getAsInt()), executor);
  }

  private final Supplier<Instant> timeSource;
  private final Supplier<Duration> nextInterval;
  private final ScheduledExecutorService executor;

  private final AtomicBoolean isRunning = new AtomicBoolean(true);
  private final TreeSet<Instant> timeline = new TreeSet<>();

  public ConcurrentClock(
      Supplier<Instant> timeSource,
      Supplier<Duration> nextInterval,
      ScheduledExecutorService executor) {
    super(executor);
    this.timeSource = timeSource;
    this.nextInterval = nextInterval;
    this.executor = executor;
  }

  @Override
  public Instant sample(Instant timestamp) {
    return timeline.headSet(timestamp).last();
  }

  @Override
  public void start() {
    synchronized (this) {
      if (!(isRunning.get() && executor.isShutdown())) {
        isRunning.set(true);
        this.executor.submit(this::tick);
      }
    }
  }

  /** Stops collecting data. If {@code mayInterruptIfRunning} is true, the data is extracted. */
  @Override
  public void stop() {
    isRunning.set(false);
  }

  /** Returns if more data will be scheduled to be collected. */
  private boolean isStopped() {
    return !isRunning.get() || executor.isShutdown();
  }

  /**
   * Collect from the {@link Supplier}, re-schedule for the next period start, and return the data.
   */
  private void tick() {
    if (isStopped()) {
      isRunning.set(false);
      return;
    }

    Instant now = this.timeSource.get();
    synchronized (this.timeline) {
      this.timeline.add(now);
    }
    downstream().forEach(signal -> executor.execute(() -> signal.update(now)));
    Duration rescheduleTime = nextInterval.get().minus(Duration.between(now, Instant.now()));

    if (!isStopped()) {
      if (rescheduleTime.toNanos() > 0) {
        // if we have some extra time, schedule the next one in the future
        executor.schedule(this::tick, rescheduleTime.toNanos(), NANOSECONDS);
      } else {
        // if we don't, run the next one immediately
        executor.submit(this::tick);
      }
    }
  }
}
