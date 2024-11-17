package sigprop2.signal.sync;

import java.time.Instant;
import java.util.function.Function;
import sigprop2.SourceSignal;

/** A {@link ProcessingSignal} that applies a function to a {@link SourceSignal}. */
final class SynchronousMappedSignal<T, U> extends SynchronousProcessingSignal<U> {
  private final SourceSignal<T> source;
  private final Function<T, U> func;

  SynchronousMappedSignal(SourceSignal<T> source, Function<T, U> func) {
    this.source = source;
    this.func = func;
  }

  /** Retrieves the data from the underlying source and applies the function to it. */
  @Override
  public final U sample(Instant timestamp) {
    return func.apply(source.sample(timestamp));
  }
}
