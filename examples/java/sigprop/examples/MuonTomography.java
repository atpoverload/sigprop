package sigprop.examples;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.signal.ClockSignal;
import sigprop.signal.GeneratingSignal;
import sigprop.signal.PropagatingSignal;
import sigprop.signal.math.scalar.ScalarRate;
import sigprop.signal.util.LoggerSink;

/**
 * An example of the Muon Detection experiment (https://en.wikipedia.org/wiki/Muon_tomography). A
 * fixed period clock is used to turn on a particle emitter. The particles are accumulated by a
 * detector until they can produce enough current to trigger the detector, causing the downstream
 * signal to compute the average particle rate.
 */
public class MuonTomography {
  private static final ScheduledExecutorService EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "sigprop-particle-emitter");
            t.setDaemon(true);
            return t;
          });

  /** A signal that simulates particles being emitted from a source. */
  private static class ParticleEmitter extends GeneratingSignal<Integer> {
    private final int shutterPeriod;

    private ParticleEmitter(int shutterPeriod) {
      super(EXECUTOR);
      this.shutterPeriod = shutterPeriod;
    }

    @Override
    public Integer generate() {
      int shutterTime = ThreadLocalRandom.current().nextInt(shutterPeriod);
      try {
        Thread.sleep(shutterTime);
      } catch (Exception e) {
      }
      return shutterTime;
    }
  }

  /** A signal that triggers each time a threshold of counts are accumulated. */
  private static class ParticleDetector extends PropagatingSignal<Integer> implements SinkSignal {
    private final SourceSignal<Integer> source;
    private final int triggerThreshold;

    private final AtomicInteger accumulatedParticles = new AtomicInteger(0);
    private final TreeMap<Instant, Integer> counts = new TreeMap<>();

    private ParticleDetector(SourceSignal<Integer> source, int triggerThreshold) {
      super(EXECUTOR);
      this.source = source;
      this.triggerThreshold = triggerThreshold;
    }

    @Override
    public Integer sample(Instant timestamp) {
      if (counts.isEmpty()) {
        return 0;
      }
      return counts.headMap(timestamp, true).lastEntry().getValue();
    }

    @Override
    public void update(Instant timestamp) {
      int totalEmitted = source.sample(timestamp).intValue();
      accumulatedParticles.set(accumulatedParticles.get() + totalEmitted);
      if (accumulatedParticles.get() > triggerThreshold) {
        int particles = accumulatedParticles.get() + sample(timestamp);
        synchronized (this) {
          counts.put(timestamp, particles);
        }
        accumulatedParticles.set(0);
        propagate(timestamp);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    ClockSignal clock = ClockSignal.fixedPeriod(Duration.ofMillis(1), EXECUTOR);
    clock
        .map(() -> new ParticleEmitter(/* shutterPeriod= */ 10))
        .asyncMap(me -> new ParticleDetector(me, /* triggerThreshold= */ 100))
        .map(me -> new ScalarRate<>(me, EXECUTOR))
        .asyncMap(LoggerSink::forSigprop);
    clock.start();

    while (true) {
      Thread.sleep(1000);
    }
  }
}
