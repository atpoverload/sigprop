package charcoal.examples;

import charcoal.SinkSignal;
import charcoal.prop.ClockSignal;
import charcoal.prop.PropagatingSignal;
import charcoal.prop.util.ConsoleSink;
import charcoal.util.Timestamps;
import java.time.Duration;
import java.time.Instant;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * An example of the Muon Detection experiment (https://en.wikipedia.org/wiki/Muon_tomography). A
 * fixed period clock is used to turn on a particle emitter. The particles are accumulated by a
 * detector until they can produce enough current to trigger the detector, causing the downstream
 * signal to compute the average particle rate.
 */
public final class Timer {
  private static final ScheduledExecutorService EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "sigprop-examples-timer");
            t.setDaemon(true);
            return t;
          });

  /** A signal that triggers each time a threshold of counts are accumulated. */
  private static class TimerSignal extends PropagatingSignal<Instant> implements SinkSignal {
    private final Duration triggerThreshold = Duration.ofMillis(1000);
    private final TreeSet<Instant> timeline = new TreeSet<>();

    private TimerSignal() {
      super(EXECUTOR);
    }

    @Override
    public final Instant sample(Instant timestamp) {
      return timeline.headSet(timestamp, true).last();
    }

    @Override
    public final void update(Instant timestamp) {
      if (timeline.isEmpty()
          || Duration.between(sample(timestamp), timestamp).compareTo(triggerThreshold) > -1) {
        synchronized (this) {
          timeline.add(timestamp);
        }
        propagate(timestamp);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    ClockSignal clock = ClockSignal.fixedPeriod(Duration.ofMillis(1), EXECUTOR);
    clock
        .map(TimerSignal::new)
        .compose(ts -> Timestamps.now())
        .mapBiFunc(Duration::between)
        .mapFunc(Duration::toNanos)
        .asyncMap(ConsoleSink::withSystemOut);
    clock.start();

    while (true) {
      Thread.sleep(1000);
    }
  }
}
