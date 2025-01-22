package sigprop;

import java.util.function.Function;
import java.util.function.Supplier;

/** A signal that can be mapped into a sink. */
public interface AsyncMappableSignal<T> extends SourceSignal<T> {
  /** Maps a sink created from a factory to this signal. */
  <S extends SinkSignal> S asyncMap(Supplier<S> sinkFactory);

  /** Maps a sink created from this signal to this signal. */
  <S extends SinkSignal> S asyncMap(Function<AsyncMappableSignal<T>, S> mapping);
}
