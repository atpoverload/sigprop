package charcoal.examples;

import charcoal.SinkSignal;
import charcoal.SourceSignal;
import charcoal.prop.ClockSignal;
import charcoal.prop.GeneratingSignal;
import charcoal.prop.PropagatingSignal;
import charcoal.prop.math.scalar.ScalarErrorPropagator;
import charcoal.prop.math.scalar.ScalarRate;
import charcoal.prop.util.ConsoleSink;
import java.time.Duration;
import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An example of the Muon Detection experiment (https://en.wikipedia.org/wiki/Muon_tomography). A
 * fixed period clock is used to turn on a particle emitter. The particles are accumulated by a
 * detector until they can produce enough current to trigger the detector, causing the downstream
 * signal to compute the average particle rate.
 */
public class MuonTomography {
  private static final ScheduledExecutorService PARTICLE_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "charcoal-examples-muon:emitter");
            t.setDaemon(true);
            return t;
          });
  private static final ScheduledExecutorService DETECTOR_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "charcoal-examples-muon:detector");
            t.setDaemon(true);
            return t;
          });

  /** A signal that simulates particles being emitted from a source. */
  private static class ParticleEmitter extends GeneratingSignal<Integer> {
    private final int shutterPeriod;

    private ParticleEmitter(int shutterPeriod) {
      super(PARTICLE_EXECUTOR);
      this.shutterPeriod = shutterPeriod;
    }

    @Override
    public Integer compute() {
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
      super(DETECTOR_EXECUTOR);
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
    ClockSignal clock =
        new ClockSignal(
            Instant::now, () -> Duration.ofMillis(1), PARTICLE_EXECUTOR, DETECTOR_EXECUTOR);
    ParticleEmitter emitter = clock.map(() -> new ParticleEmitter(/* shutterPeriod= */ 10));
    emitter
        .asyncMap(me -> new ParticleDetector(me, /* triggerThreshold= */ 100))
        .map(me -> new ScalarRate<>(me, DETECTOR_EXECUTOR))
        .asyncMap(me -> new ScalarErrorPropagator<>(me, DETECTOR_EXECUTOR))
        .map(ConsoleSink::withSystemOut);
    clock.start();

    while (true) {
      Thread.sleep(1000);
    }
  }
}
