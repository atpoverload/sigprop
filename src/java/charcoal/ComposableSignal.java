package charcoal;

/** A signal that can be composed with another signal into another sink. */
public interface ComposableSignal<T> {
  /** Composes this signal with another signal . */
  <U> ComposedSignal<T, U> compose(SourceSignal<U> other);

  /** Composes this signal with another signal . */
  <U> ComposedSignal<T, U> asyncCompose(SourceSignal<U> other);
}
