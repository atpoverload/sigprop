package sigprop.signal.math.scalar;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop.SourceSignal;
import sigprop.signal.ComposingSignal;

/** A {@ComposingSignal} that computes the product of two number signals. */
public final class ScalarProduct<T extends Number, U extends Number>
    extends ComposingSignal<T, U, Double> {

  public ScalarProduct(SourceSignal<T> first, SourceSignal<U> second, Executor executor) {
    super(first, second, executor);
  }

  /** Returns the ratio of the inputs. */
  @Override
  public Double compute(Instant timestamp, T first, U second) {
    return first.doubleValue() * second.doubleValue();
  }
}
