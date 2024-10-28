package sigprop.signal;

import java.time.Instant;
import java.util.function.Function;
import sigprop.SinkSignal;
import sigprop.SourceSignal;

/**
 * A {@link Signal} that applies a function to a {@link SourceSignal} and can be consumed by another
 * signal.
 */
public abstract class MappedSignal<T, U> extends SubscribeableSignal<U> implements SinkSignal<U> {
  private final SourceSignal<T> source;
  private final Function<T, U> func;

  public MappedSignal(SourceSignal<T> source, Function<T, U> func) {
    this.source = source;
    this.func = func;
  }

  /** Retrieves the data from the underlying source and applies the function to it. */
  @Override
  public U sample(Instant timestamp) {
    return func.apply(source.sample(timestamp));
  }

  /** Informs all downstream signals to update. */
  @Override
  public void update(Instant timestamp) {
    downstream().forEach(signal -> updateSignal(timestamp, signal));
  }

  /** Updates a specific signal. */
  protected abstract void updateSignal(Instant timestamp, SinkSignal<?> signal);
}
