package sigprop3.signal.math.scalar;

import java.time.Duration;
import java.time.Instant;
import sigprop3.SourceSignal;
import sigprop3.signal.sync.SynchronousTimelineSignal;

/**
 * A {@link SynchronousTimelineSignal} that computes the forward difference (i.e. the rate) between
 * two numbers (i.e. a scalar). If no data has been seen, then the rate must be zero.
 */
public final class ScalarForwardDifference<N extends Number>
    extends SynchronousTimelineSignal<N, Double> {
  public ScalarForwardDifference(SourceSignal<N> source) {
    super(source);
  }

  /** Returns zero, which is the sanest value for no data. */
  @Override
  protected Double defaultSignal() {
    return 0.0;
  }

  /** Returns the ratio of the differences of the values and the timestamps. */
  @Override
  protected Double computeSignal(Instant start, Instant end, Number first, Number second) {
    double flux = second.doubleValue() - first.doubleValue();
    double elapsed = ((double) Duration.between(start, end).toNanos()) / 1000000000.0;
    return flux / elapsed;
  }
}
