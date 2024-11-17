package sigprop3;

import java.time.Instant;

/**
 * A signal that has a value at all {@link Instants}, and whose values can be consumed by a {@link
 * SinkSignal}.
 */
public interface SourceSignal<T> {
  /** Returns the value of the signal at the given {@link Instant}. */
  T sample(Instant timestamp);

  /** Adds a sink that will be updated by this signal. */
  <S extends SinkSignal> S sink(S sink);
}
