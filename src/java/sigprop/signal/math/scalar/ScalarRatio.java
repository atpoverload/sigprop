package sigprop.signal.math.scalar;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop.SourceSignal;
import sigprop.signal.ComposingSignal;

/** A {@ComposingSignal} that computes the ratio of two number signals. */
public final class ScalarRatio<T extends Number, U extends Number>
    extends ComposingSignal<T, U, Double> {

  public ScalarRatio(SourceSignal<T> first, SourceSignal<U> second, Executor executor) {
    super(first, second, executor);
  }

  /** Returns the ratio of the inputs. */
  @Override
  public Double compute(Instant timestamp, T first, U second) {
    return first.doubleValue() / second.doubleValue();
  }
}
