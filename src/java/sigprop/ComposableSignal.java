package sigprop;

import java.util.function.BiFunction;

/** A signal that can be composed with another signal into another sink. */
public interface ComposableSignal<T> extends MappableSignal<T> {
  /** Composes this signal with another signal . */
  <U, S extends SinkSignal> S compose(
      BiFunction<ComposableSignal<T>, ComposableSignal<U>, S> composition,
      ComposableSignal<U> other);
}
