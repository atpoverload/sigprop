package sigprop.signal;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop.SinkSignal;
import sigprop.SourceSignal;

public abstract class MappingSignal<T, U> extends PropagatingSignal<U> implements SinkSignal {
  private final SourceSignal<T> source;

  protected MappingSignal(SourceSignal<T> source, Executor executor) {
    super(executor);
    this.source = source;
  }

  /**
   * Returns a computation done over an interval, i.e., the region of time between two events of
   * some type. Users should implement the {@code defaultSignal} and {@code computeSignal} methods.
   */
  @Override
  public final U sample(Instant timestamp) {
    return compute(timestamp, source.sample(timestamp));
  }

  /** Informs all downstream signals to update if there are at least two updates. */
  @Override
  public final void update(Instant timestamp) {
    propagate(timestamp);
  }

  /** Computes the signal value over the given interval. */
  protected abstract U compute(Instant timestamp, T value);
}