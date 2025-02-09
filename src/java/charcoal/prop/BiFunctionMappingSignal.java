package charcoal.prop;

import charcoal.SourceSignal;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

class BiFunctionMappingSignal<T, U, V> extends BiMappingSignal<T, U, V> {
  private final BiFunction<T, U, V> func;

  BiFunctionMappingSignal(
      SourceSignal<T> first, SourceSignal<U> second, BiFunction<T, U, V> func, Executor executor) {
    super(first, second, executor);
    this.func = func;
  }

  /** Computes the signal value over the given interval. */
  @Override
  protected V compute(Instant timestamp, T first, U second) {
    return func.apply(first, second);
  }
}
