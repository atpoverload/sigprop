package sigprop.signal;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop.SinkSignal;
import sigprop.SourceSignal;

public abstract class ComposingSignal<T, U, V> extends PropagatingSignal<V> implements SinkSignal {
  private final SourceSignal<T> first;
  private final SourceSignal<U> second;

  protected ComposingSignal(SourceSignal<T> first, SourceSignal<U> second, Executor executor) {
    super(executor);
    this.first = first;
    this.second = second;
  }

  /**
   * Returns a computation done over an interval, i.e., the region of time between two events of
   * some type. Users should implement the {@code defaultSignal} and {@code computeSignal} methods.
   */
  @Override
  public final V sample(Instant timestamp) {
    return compute(timestamp, first.sample(timestamp), second.sample(timestamp));
  }

  /** Informs all downstream signals to update if there are at least two updates. */
  @Override
  public final void update(Instant timestamp) {
    propagate(timestamp);
  }

  /** Computes the signal value over the given interval. */
  protected abstract V compute(Instant timestamp, T first, U second);
}
