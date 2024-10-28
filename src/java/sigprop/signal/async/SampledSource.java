package sigprop.signal.async;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import sigprop.signal.SubscribeableSignal;
import sigprop.util.LoggerUtil;
import sigprop.util.Timestamps;

/**
 * A {@link Future} that allows for collecting many pieces of data from a {@link Supplier}. Note
 * that since this class uses a {@link ScheduledExecutorService}, the sampling precision is limited
 * by the executor's precision, assuming the data source is not slow. Out of the box impls, like
 * {@link Executors.newScheduledThreadPool} seems to behave well at 1-2ms (system dependent).
 */
public final class SampledSource<T> extends SubscribeableSignal<T> {
  private static final Logger logger = LoggerUtil.getLogger();

  /** Start a {@link SampledSource} that samples at a fixed {@link Duration}. */
  public static <T> SampledSource<T> fixedPeriod(
      Supplier<? extends T> source, Duration period, ScheduledExecutorService executor) {
    return fromPeriodSupplier(() -> source.get(), () -> period, executor);
  }

  /** Start a {@link SampledSource} that samples at a fixed millisecond period. */
  public static <T> SampledSource<T> fixedPeriodMillis(
      Supplier<? extends T> source, int periodMillis, ScheduledExecutorService executor) {
    Duration period = Duration.ofMillis(periodMillis);
    return fixedPeriod(source, period, executor);
  }

  /** Start a {@link SampledSource} that samples using a {@link Supplier} of {@link Durations}. */
  public static <T> SampledSource<T> fromPeriodSupplier(
      Supplier<? extends T> source,
      Supplier<Duration> periodSupplier,
      ScheduledExecutorService executor) {
    return new SampledSource<>(() -> source.get(), periodSupplier, executor);
  }

  /** Start a {@link SampledSource} that samples using an {@link IntSupplier} of milliseconds. */
  public static <T> SampledSource<T> fromMillisSupplier(
      Supplier<? extends T> source,
      IntSupplier periodMillisSupplier,
      ScheduledExecutorService executor) {
    return fromPeriodSupplier(
        source, () -> Duration.ofMillis(periodMillisSupplier.getAsInt()), executor);
  }

  private final Supplier<Duration> nextInterval;
  private final ScheduledExecutorService executor;

  private final AtomicBoolean isCollecting = new AtomicBoolean(true);
  private final TreeMap<Instant, T> timeline = new TreeMap<>();

  public SampledSource(
      Supplier<? extends T> source,
      Supplier<Duration> nextInterval,
      ScheduledExecutorService executor) {
    this.nextInterval = nextInterval;
    this.executor = executor;
    this.executor.submit(() -> collectNextSample(source));
  }

  @Override
  public T sample(Instant timestamp) {
    return timeline.floorEntry(timestamp).getValue();
  }

  /** Stops collecting data. If {@code mayInterruptIfRunning} is true, the data is extracted. */
  public void stop() {
    // this will kill all pending futures
    isCollecting.set(false);
  }

  /** Returns if more data will be scheduled to be collected. */
  private boolean isCancelled() {
    return !isCollecting.get() || executor.isShutdown();
  }

  /**
   * Collect from the {@link Supplier}, re-schedule for the next period start, and return the data.
   */
  private void collectNextSample(Supplier<? extends T> source) {
    if (isCancelled()) {
      isCollecting.set(false);
      return;
    }

    // TODO: need some sort of safety mechanism so this doesn't kill the chain on throw
    Instant start = Timestamps.now();
    try {
      T sample = source.get();
      synchronized (this.timeline) {
        this.timeline.put(start, sample);
      }
      logger.fine(String.format("collected sample (%s)", sample));
      downstream().forEach(signal -> executor.execute(() -> signal.update(start)));
      logger.fine("sent 'update' downstream signals");
    } catch (Exception e) {
      logger.log(Level.WARNING, "failed to sample from the given data source", e);
    }
    Duration rescheduleTime = nextInterval.get().minus(Duration.between(start, Timestamps.now()));

    if (!isCancelled()) {
      if (rescheduleTime.toNanos() > 0) {
        // if we have some extra time, schedule the next one in the future
        executor.schedule(() -> collectNextSample(source), rescheduleTime.toNanos(), NANOSECONDS);
      } else {
        // if we don't, run the next one immediately
        executor.submit(() -> collectNextSample(source));
      }
    }
  }
}
