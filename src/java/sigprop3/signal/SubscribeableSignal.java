package sigprop3.signal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import sigprop3.ComposableSignal;
import sigprop3.MappableSignal;
import sigprop3.SinkSignal;

/** A signal that has a downstream of signals it updates. */
public abstract class SubscribeableSignal<T> implements ComposableSignal<T>, MappableSignal<T> {
  private final ArrayList<SinkSignal> downstream = new ArrayList<>();

  /** Adds a {@link SinkSignal} to the downstream. */
  @Override
  public final <S extends SinkSignal> S sink(S sink) {
    downstream.add(sink);
    return sink;
  }

  /** Creates a new {@link SinkSignal} to add to the downstream. */
  @Override
  public final <S extends SinkSignal> S map(Supplier<S> sinkFactory) {
    return this.sink(sinkFactory.get());
  }

  /** Creates a {@link SinkSignal} using this signal to add to the downstream. */
  @Override
  public final <S extends SinkSignal> S map(Function<MappableSignal<T>, S> mapping) {
    return this.sink(mapping.apply(this));
  }

  /**
   * Creates a {@link SinkSignal} using this signal and another composable signal to add to the
   * downstream.
   */
  @Override
  public final <U, S extends SinkSignal> S compose(
      BiFunction<ComposableSignal<T>, ComposableSignal<U>, S> composition,
      ComposableSignal<U> other) {
    final S sink = composition.apply(this, other);
    this.sink(sink);
    other.sink(sink);
    return sink;
  }

  /** Updates all signals using {@code updateSignal}. */
  protected final void updateDownstream(Instant now) {
    downstream.forEach(signal -> updateSignal(signal, now));
  }

  /** The individual method that updates a downstream signal. */
  protected abstract <S extends SinkSignal> void updateSignal(S signal, Instant timestamp);
}
