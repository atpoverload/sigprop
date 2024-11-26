package sigprop3.signal;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/** A {@link SubscribeableSignal}that {@code ticks} periodically to update its downstream. */
public abstract class SubscribeableClockSignal extends SubscribeableSignal<Instant>
    implements ClockSignal {
  private final Supplier<Instant> timeSource;
  private final Supplier<Duration> nextInterval;
  private final ScheduledExecutorService executor;

  private final AtomicBoolean isRunning = new AtomicBoolean(false);
  private final TreeSet<Instant> timeline = new TreeSet<>();
  private final AtomicReference<Instant> lastTick = new AtomicReference<>();

  protected SubscribeableClockSignal(
      Supplier<Instant> timeSource,
      Supplier<Duration> nextInterval,
      ScheduledExecutorService executor) {
    this.timeSource = timeSource;
    this.nextInterval = nextInterval;
    this.executor = executor;
  }

  /** Returns the timestamp closest to the given one. */
  @Override
  public final Instant sample(Instant timestamp) {
    return timeline.headSet(timestamp).last();
  }

  /** Returns the most recent timestamp. */
  @Override
  public final Instant lastTick() {
    return this.timeline.last();
  }

  /** Starts the clock, which will update its downstream. */
  @Override
  public final void start() {
    synchronized (this) {
      if (!(isRunning.get() && executor.isShutdown())) {
        isRunning.set(true);
        this.executor.submit(this::tick);
      }
    }
  }

  /** Stops the clock. */
  @Override
  public final void stop() {
    isRunning.set(false);
  }

  /** Returns if the clock is currently running (i.e. the downstream will be updated). */
  private boolean isStopped() {
    return !isRunning.get() || executor.isShutdown();
  }

  /** Updates the downstream with the current timestamp and then schedules the next tick. */
  private void tick() {
    if (isStopped()) {
      isRunning.set(false);
      return;
    }

    Instant now = this.timeSource.get();
    synchronized (this.timeline) {
      this.timeline.add(now);
    }
    updateDownstream(now);
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
