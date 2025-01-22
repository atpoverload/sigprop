package sigprop;

import java.time.Instant;

/** A signal that contains continuous time data. */
public interface SourceSignal<T> {
  /** Returns the value of the signal at the given {@link Instant}. */
  T sample(Instant timestamp);
}
