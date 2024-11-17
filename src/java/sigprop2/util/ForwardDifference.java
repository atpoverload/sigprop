package sigprop2.util;

import java.time.Duration;
import java.time.Instant;
import sigprop2.SourceSignal;
import sigprop2.signal.sync.SynchronousTimelineSignal;

/**
 * A signal that computes the forward difference (i.e. the rate) between two numbers. If no data has
 * been seen, then the rate must be zero.
 */
public class ForwardDifference<T extends Number> extends SynchronousTimelineSignal<T, Double> {
  public ForwardDifference(SourceSignal<T> source) {
    super(source);
  }

  @Override
  protected Double defaultSignalValue() {
    return 0.0;
  }

  @Override
  protected Double computeSignalValue(Instant start, Instant end, Number first, Number second) {
    double flux = second.doubleValue() - first.doubleValue();
    double elapsed = ((double) Duration.between(start, end).toNanos()) / 1000000000.0;
    return flux / elapsed;
  }
}
