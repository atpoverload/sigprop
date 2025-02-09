package charcoal.prop;

import charcoal.SourceSignal;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.Function;

final class FunctionMappingSignal<T, U> extends MappingSignal<T, U> {
  private final Function<T, U> func;

  protected FunctionMappingSignal(SourceSignal<T> source, Function<T, U> func, Executor executor) {
    super(source, executor);
    this.func = func;
  }

  /** Computes the signal value over the given interval. */
  protected U compute(Instant timestamp, T value) {
    return func.apply(value);
  }
}
