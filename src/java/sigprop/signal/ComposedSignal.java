package sigprop.signal;

import java.time.Instant;
import java.util.function.BiFunction;
import sigprop.Signal;
import sigprop.SinkSignal;

/**
 * A {@link Signal} that applies a function to two {@link SourceSignals} and can be consumed by
 * another signal.
 */
public abstract class ComposedSignal<T, U, V> extends SubscribeableSignal<V>
    implements SinkSignal<V> {
  private final Signal<T> first;
  private final Signal<U> second;
  private final BiFunction<T, U, V> func;

  protected ComposedSignal(Signal<T> first, Signal<U> second, BiFunction<T, U, V> func) {
    this.first = first;
    this.second = second;
    this.func = func;
  }

  /** Retrieves the data from the underlying sources and applies the function to it. */
  @Override
  public V sample(Instant timestamp) {
    return func.apply(first.sample(timestamp), second.sample(timestamp));
  }

  /** Informs all downstream signals to update. */
  @Override
  public void update(Instant timestamp) {
    downstream().forEach(signal -> updateSignal(timestamp, signal));
  }

  protected abstract void updateSignal(Instant timestamp, SinkSignal<?> signal);
}
