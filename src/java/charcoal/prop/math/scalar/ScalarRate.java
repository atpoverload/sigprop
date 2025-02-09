package charcoal.prop.math.scalar;

import charcoal.SourceSignal;
import charcoal.prop.AdjacentTimelineSignal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;

/** A {@link AdjacentTimelineSignal} that computes the scalar rate between two numbers signals. */
public final class ScalarRate<T extends Number> extends AdjacentTimelineSignal<T, Double> {
  public ScalarRate(SourceSignal<T> source, Executor executor) {
    super(source, executor);
  }

  /** Returns the ratio of the differences of the values and the timestamps. */
  @Override
  protected Double compute(Instant start, Instant end, T first, T second) {
    if (start.equals(end)) {
      return 0.0;
    }
    double flux = second.doubleValue() - first.doubleValue();
    double elapsed = ((double) Duration.between(start, end).toNanos()) / 1000000000.0;
    return flux / elapsed;
  }
}
