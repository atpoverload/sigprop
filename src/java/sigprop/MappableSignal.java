package sigprop;

import java.util.function.Function;
import java.util.function.Supplier;

/** A signal that can be transformed into a {@link SinkSignal}. */
public interface MappableSignal<T> extends SourceSignal<T> {
  /** Creates a sink connected to this signal. */
  <S extends SinkSignal> S map(Supplier<S> sinkFactory);

  /** Creates a sink connected to this signal, which consumes this signal. */
  <S extends SinkSignal> S map(Function<MappableSignal<T>, S> mapping);
}
