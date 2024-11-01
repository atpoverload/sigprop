package sigprop2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

/** A signal that has a downstream of signals it feeds into. */
public abstract class SourceSignal<T> {
  private final ArrayList<SinkSignal> downstream = new ArrayList<>();

  /** Returns the value of the signal at the given {@link Instant}. */
  public abstract T sample(Instant timestamp);

  /** Adds a {@link SinkSignal} to the downstream. */
  public final <S extends SinkSignal> S map(Function<? super SourceSignal<T>, S> mapping) {
    final S sink = mapping.apply(this);
    downstream.add(sink);
    return sink;
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  public final <U, V, S extends SinkSignal> S compose(
      SourceSignal<U> other,
      BiFunction<? super SourceSignal<T>, ? super SourceSignal<U>, S> composition) {
    final S sink = composition.apply(this, other);
    this.downstream.add(sink);
    other.downstream.add(sink);
    return sink;
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  public abstract <U> SourceSignal<U> mapFunc(Function<T, U> func);

  /** Adds a {@link SinkSignal} to the downstream. */
  public abstract <U, V> SourceSignal<V> composeFunc(
      SourceSignal<U> other, BiFunction<T, U, V> func);

  /** Returns a shallow copy of the downstream {@link SinkSignals}. */
  protected final Iterable<SinkSignal> downstream() {
    return new ArrayList<>(downstream);
  }
}
