package charcoal;

import java.util.function.Function;
import java.util.function.Supplier;

/** A signal that can be transformed into a {@link SinkSignal}. */
public interface MappableSignal<T> {
  /** Creates a sink connected to this signal. */
  <S extends SinkSignal> S map(Supplier<S> sinkFactory);

  /** Creates a sink connected to this signal, which consumes this signal. */
  <S extends SinkSignal> S map(Function<SourceSignal<T>, S> mapping);

  /** Maps a sink created from a factory to this signal. */
  <S extends SinkSignal> S asyncMap(Supplier<S> sinkFactory);

  /** Maps a sink created from this signal to this signal. */
  <S extends SinkSignal> S asyncMap(Function<SourceSignal<T>, S> mapping);
}
