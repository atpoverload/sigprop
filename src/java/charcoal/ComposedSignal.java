package charcoal;

import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ComposedSignal<T, U> {
  /** Creates a sink connected to this signal. */
  <S extends SinkSignal> S map(Supplier<S> sinkFactory);

  /** Creates a sink connected to this signal, which consumes this signal. */
  <S extends SinkSignal> S map(Function<SourceSignal<Entry<T, U>>, S> mapping);

  /** Creates a sink connected to this signal, which consumes this signal. */
  <S extends SinkSignal> S map(BiFunction<SourceSignal<T>, SourceSignal<U>, S> mapping);

  /** Creates a sink connected to this signal. */
  <S extends SinkSignal> S asyncMap(Supplier<S> sinkFactory);

  /** Creates a sink connected to this signal, which consumes this signal. */
  <S extends SinkSignal> S asyncMap(Function<SourceSignal<Entry<T, U>>, S> mapping);

  /** Creates a sink connected to this signal, which consumes this signal. */
  <S extends SinkSignal> S asyncMap(BiFunction<SourceSignal<T>, SourceSignal<U>, S> mapping);
}
