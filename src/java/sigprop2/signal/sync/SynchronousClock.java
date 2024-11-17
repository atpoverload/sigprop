package sigprop2.signal.sync;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import sigprop2.signal.Clock;

public final class SynchronousClock extends SynchronousSubscribeableSignal<Instant>
    implements Clock {
  /** Start a {@link SynchronousClock} that samples at a fixed {@link Duration}. */
  public static SynchronousClock fixedPeriod(Duration period, ScheduledExecutorService executor) {
    return fromPeriodSupplier(() -> period, executor);
  }

  /** Start a {@link SynchronousClock} that samples at a fixed millisecond period. */
  public static SynchronousClock fixedPeriodMillis(
      int periodMillis, ScheduledExecutorService executor) {
    Duration period = Duration.ofMillis(periodMillis);
    return fixedPeriod(period, executor);
  }

  /**
   * Start a {@link SynchronousClock} that samples using a {@link Supplier} of {@link Durations}.
   */
  public static SynchronousClock fromPeriodSupplier(
      Supplier<Duration> periodSupplier, ScheduledExecutorService executor) {
    return new SynchronousClock(Instant::now, periodSupplier, executor);
  }

  /** Start a {@link SynchronousClock} that samples using an {@link IntSupplier} of milliseconds. */
  public static SynchronousClock fromMillisSupplier(
      IntSupplier periodMillisSupplier, ScheduledExecutorService executor) {
    return fromPeriodSupplier(() -> Duration.ofMillis(periodMillisSupplier.getAsInt()), executor);
  }

  private final Supplier<Instant> timeSource;
  private final Supplier<Duration> nextInterval;
  private final ScheduledExecutorService executor;

  private final AtomicBoolean isRunning = new AtomicBoolean(false);
  private final TreeSet<Instant> timeline = new TreeSet<>();

  public SynchronousClock(
      Supplier<Instant> timeSource,
      Supplier<Duration> nextInterval,
      ScheduledExecutorService executor) {
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

  /** Updates all downstream */
  private void tick() {
    if (isStopped()) {
      isRunning.set(false);
      return;
    }

    Instant now = this.timeSource.get();
    synchronized (this.timeline) {
      this.timeline.add(now);
    }
    downstream().forEach(signal -> signal.update(now));
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
