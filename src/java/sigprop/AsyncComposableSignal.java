package sigprop;

import java.util.function.BiFunction;

/** A signal that can be composed with another signal into another sink. */
public interface AsyncComposableSignal<T> extends AsyncMappableSignal<T> {
  /** Composes this signal with another signal . */
  <U, S extends SinkSignal> S asyncCompose(
      BiFunction<AsyncComposableSignal<T>, AsyncComposableSignal<U>, S> composition,
      AsyncComposableSignal<U> other);
}
