package sigprop2.signal.sync;

import java.time.Instant;
import java.util.function.BiFunction;
import sigprop2.SourceSignal;

/** A {@link ProcessingSignal} that applies a function to two {@link SourceSignals}. */
final class SynchronousComposedSignal<T, U, V> extends SynchronousProcessingSignal<V> {
  private final SourceSignal<T> first;
  private final SourceSignal<U> second;
  private final BiFunction<T, U, V> func;

  SynchronousComposedSignal(
      SourceSignal<T> first, SourceSignal<U> second, BiFunction<T, U, V> func) {
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
