package sigprop3;

import java.util.function.Function;
import java.util.function.Supplier;

/** A signal that can be mapped into a sink. */
public interface MappableSignal<T> extends SourceSignal<T> {
  /** Maps a sink created from a factory to this signal. */
  <S extends SinkSignal> S map(Supplier<S> sinkFactory);

  /** Maps a sink created from this signal to this signal. */
  <S extends SinkSignal> S map(Function<MappableSignal<T>, S> mapping);
}
