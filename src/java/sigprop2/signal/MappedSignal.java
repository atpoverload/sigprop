package sigprop2.signal;

import java.time.Instant;
import java.util.function.Function;
import sigprop2.SourceSignal;

/** A {@link ProcessingSignal} that applies a function to a {@link SourceSignal}. */
final class MappedSignal<T, U> extends ProcessingSignal<U> {
  private final SourceSignal<T> source;
  private final Function<T, U> func;

  MappedSignal(SourceSignal<T> source, Function<T, U> func) {
    this.source = source;
    this.func = func;
  }

  /** Retrieves the data from the underlying source and applies the function to it. */
  @Override
  public final U sample(Instant timestamp) {
    return func.apply(source.sample(timestamp));
  }
}
