package charcoal.prop.math.scalar;

import charcoal.SourceSignal;
import charcoal.prop.BiMappingSignal;
import java.time.Instant;
import java.util.concurrent.Executor;

/** A {@ComposingSignal} that computes the difference of two number signals. */
public final class ScalarDifference<T extends Number, U extends Number>
    extends BiMappingSignal<T, U, Double> {

  public ScalarDifference(SourceSignal<T> first, SourceSignal<U> second, Executor executor) {
    super(first, second, executor);
  }

  /** Returns the ratio of the inputs. */
  @Override
  public Double compute(Instant timestamp, T first, U second) {
    return first.doubleValue() - second.doubleValue();
  }
}
