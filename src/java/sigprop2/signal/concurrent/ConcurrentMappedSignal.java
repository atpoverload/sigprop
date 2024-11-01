package sigprop2.signal.concurrent;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.Function;
import sigprop2.SourceSignal;

/** A {@link ConcurrentProcessingSignal} that applies a function to a {@link SourceSignal}. */
final class ConcurrentMappedSignal<T, U> extends ConcurrentProcessingSignal<U> {
  private final SourceSignal<T> source;
  private final Function<T, U> func;

  ConcurrentMappedSignal(SourceSignal<T> source, Function<T, U> func, Executor executor) {
    super(executor);
    this.source = source;
    this.func = func;
  }

  /** Retrieves the data from the underlying source and applies the function to it. */
  @Override
  public final U sample(Instant timestamp) {
    return func.apply(source.sample(timestamp));
  }
}
