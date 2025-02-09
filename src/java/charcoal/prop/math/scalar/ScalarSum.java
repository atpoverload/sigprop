package charcoal.prop.math.scalar;

import charcoal.SourceSignal;
import charcoal.prop.BiMappingSignal;
import java.time.Instant;
import java.util.concurrent.Executor;

/** A {@BiMappingSignal} that computes the sum of two number signals. */
public final class ScalarSum<T extends Number, U extends Number>
    extends BiMappingSignal<T, U, Double> {

  public ScalarSum(SourceSignal<T> first, SourceSignal<U> second, Executor executor) {
    super(first, second, executor);
  }

  /** Returns the ratio of the inputs. */
  @Override
  public Double compute(Instant timestamp, T first, U second) {
    return first.doubleValue() + second.doubleValue();
  }
}
