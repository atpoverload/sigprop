package sigprop.signal.math.scalar;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop.SourceSignal;
import sigprop.signal.TimelineSignal;

/** A {@link TimelineSignal} that computes the scalar rate between two numbers signals. */
public final class ScalarRate<N extends Number> extends TimelineSignal<N, Double> {
  public ScalarRate(SourceSignal<N> source, Executor executor) {
    super(source, executor);
  }

  /** Returns the ratio of the differences of the values and the timestamps. */
  @Override
  protected Double compute(Instant start, Instant end, Number first, Number second) {
    if (start.equals(end)) {
      return 0.0;
    }
    double flux = second.doubleValue() - first.doubleValue();
    double elapsed = ((double) Duration.between(start, end).toNanos()) / 1000000000.0;
    return flux / elapsed;
  }
}
