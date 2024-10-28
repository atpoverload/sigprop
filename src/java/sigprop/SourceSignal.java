package sigprop;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface SourceSignal<T> extends Signal<T> {
  /** Connects this signal to a {@link SinkSignal} which will be updated by this signal. */
  <U, S extends SinkSignal<U>> S map(Function<SourceSignal<T>, S> sinkFactory);

  /** Composes this signal with another {@link Signal} and maps their values to another signal. */
  <U, V, S extends SinkSignal<V>> S compose(
      SourceSignal<U> other, BiFunction<SourceSignal<T>, SourceSignal<U>, S> sinkFactory);
}
