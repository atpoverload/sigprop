package sigprop3.examples;

import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import sigprop3.SourceSignal;
import sigprop3.signal.concurrent.ConcurrentProcessingSignal;
import sigprop3.signal.math.scalar.ScalarForwardDifference;
import sigprop3.signal.sync.SynchronousClockSignal;
import sigprop3.signal.sync.SynchronousProcessingSignal;
import sigprop3.signal.util.ConsoleSink;

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
            Thread t = new Thread(r, "sigprop3-particle-emitter");
            t.setDaemon(true);
            return t;
          });

  /** A signal that simulates particles being emitted from a source. */
  private static class ParticleEmitter extends ConcurrentProcessingSignal<Integer> {
    private final int shutterPeriod;

    private final AtomicInteger particlesEmitted = new AtomicInteger();

    private ParticleEmitter(int shutterPeriod) {
      super(EXECUTOR);
      this.shutterPeriod = shutterPeriod;
    }

    @Override
    public final Integer sample(Instant timestamp) {
      return particlesEmitted.get();
    }

    @Override
    public final void update(Instant timestamp) {
      int shutterTime = ThreadLocalRandom.current().nextInt(shutterPeriod);
      try {
        Thread.sleep(shutterTime);
      } catch (Exception e) {
      }
      particlesEmitted.addAndGet(shutterTime);
      updateDownstream(timestamp);
    }
  }

  /** A signal that triggers each time a threshold of counts are accumulated. */
  private static class ParticleDetector extends SynchronousProcessingSignal<Integer> {
    private final SourceSignal<Integer> source;
    private final int triggerThreshold;

    private final AtomicInteger accumulatedParticles = new AtomicInteger(0);
    private final TreeMap<Instant, Integer> counts = new TreeMap<>();

    private ParticleDetector(SourceSignal<Integer> source, int triggerThreshold) {
      this.source = source;
      this.triggerThreshold = triggerThreshold;
    }

    @Override
    public final Integer sample(Instant timestamp) {
      return counts.floorEntry(timestamp).getValue();
    }

    @Override
    public final void update(Instant timestamp) {
      int totalEmitted = source.sample(timestamp).intValue();
      if (totalEmitted - accumulatedParticles.get() > triggerThreshold) {
        synchronized (this) {
          accumulatedParticles.set(totalEmitted);
          counts.put(timestamp, totalEmitted);
          updateDownstream(timestamp);
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    SynchronousClockSignal clock = SynchronousClockSignal.fixedPeriodMillis(1, EXECUTOR);
    clock
        .sink(new ParticleEmitter(/* shutterPeriod= */ 10))
        .map(me -> new ParticleDetector(me, /* triggerThreshold= */ 100))
        .map(ScalarForwardDifference::new)
        .map(ConsoleSink::withSystemOut);
    clock.start();

    while (true) {
      Thread.sleep(1000);
    }
  }
}
