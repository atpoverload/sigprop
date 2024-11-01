package sigprop2.signal.concurrent;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import sigprop2.SourceSignal;

/** A {@link ConcurrentProcessingSignal} that applies a function to two {@link SourceSignals}. */
final class ConcurrentComposedSignal<T, U, V> extends ConcurrentProcessingSignal<V> {
  private final SourceSignal<T> first;
  private final SourceSignal<U> second;
  private final BiFunction<T, U, V> func;

  ConcurrentComposedSignal(
      SourceSignal<T> first, SourceSignal<U> second, BiFunction<T, U, V> func, Executor executor) {
    super(executor);
    this.first = first;
    this.second = second;
    this.func = func;
  }

  /** Retrieves the data from the underlying sources and applies the function to it. */
  @Override
  public final V sample(Instant timestamp) {
    return func.apply(first.sample(timestamp), second.sample(timestamp));
  }
}
