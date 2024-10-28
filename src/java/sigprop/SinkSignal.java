package sigprop;

import java.time.Instant;

/** A {@link Signal} whose value can be updated. */
public interface SinkSignal<T> extends Signal<T> {
  /** Informs the {@code SinkSignal} that a new value is available at the given timestamp. */
  void update(Instant timestamp);
}
