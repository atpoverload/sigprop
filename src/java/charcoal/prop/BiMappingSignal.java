package charcoal.prop;

import charcoal.SourceSignal;
import java.time.Instant;
import java.util.concurrent.Executor;

public abstract class BiMappingSignal<T, U, V> extends DelegatingSignal<V> {
  private final SourceSignal<T> first;
  private final SourceSignal<U> second;

  protected BiMappingSignal(SourceSignal<T> first, SourceSignal<U> second, Executor executor) {
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

  /** Computes the signal value over the given interval. */
  protected abstract V compute(Instant timestamp, T first, U second);
}
