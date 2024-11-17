package sigprop2;

import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/** A signal that has a value at all {@link Instants}. */
public interface SourceSignal<T> {
  /** Returns the value of the signal at the given {@link Instant}. */
  T sample(Instant timestamp);

  /** Adds and returns a {@link SinkSignal}. */
  <S extends SinkSignal> S sink(S sink);

  /** Adds and returns a {@link SinkSignal}. */
  <S extends SinkSignal> S map(Supplier<S> sinkFactory);

  /** Adds and returns a {@link SinkSignal}. */
  <S extends SinkSignal> S map(Function<? super SourceSignal<T>, S> mapping);

  /** Adds and returns a {@link SinkSignal}. */
  <U, V, S extends SinkSignal> S compose(
      BiFunction<? super SourceSignal<T>, ? super SourceSignal<U>, S> composition,
      SourceSignal<U> other);

  /** Adds and returns a {@link SinkSignal}. */
  <U> SourceSignal<U> mapFunc(Function<T, U> func);

  /** Adds and returns a {@link SinkSignal}. */
  <U, V> SourceSignal<V> composeFunc(BiFunction<T, U, V> func, SourceSignal<U> other);
}
