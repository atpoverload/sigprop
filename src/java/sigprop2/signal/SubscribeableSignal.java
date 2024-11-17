package sigprop2.signal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import sigprop2.SinkSignal;
import sigprop2.SourceSignal;

/** A signal that has a downstream of signals it feeds into. */
public abstract class SubscribeableSignal<T> implements SourceSignal<T> {
  private final ArrayList<SinkSignal> downstream = new ArrayList<>();

  /** Returns the value of the signal at the given {@link Instant}. */
  public abstract T sample(Instant timestamp);

  /** Adds a {@link SinkSignal} to the downstream. */
  public final <S extends SinkSignal> S sink(S sink) {
    downstream.add(sink);
    return sink;
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  public final <S extends SinkSignal> S map(Supplier<S> sinkFactory) {
    return this.sink(sinkFactory.get());
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  public final <S extends SinkSignal> S map(Function<? super SourceSignal<T>, S> mapping) {
    return this.sink(mapping.apply(this));
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  public final <U, V, S extends SinkSignal> S compose(
      BiFunction<? super SourceSignal<T>, ? super SourceSignal<U>, S> composition,
      SourceSignal<U> other) {
    final S sink = composition.apply(this, other);
    this.sink(sink);
    other.sink(sink);
    return sink;
  }

  /** Returns a shallow copy of the downstream {@link SinkSignals}. */
  protected final Iterable<SinkSignal> downstream() {
    return new ArrayList<>(downstream);
  }
}
