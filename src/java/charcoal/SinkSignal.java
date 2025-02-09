package charcoal;

import java.time.Instant;

/** A signal that can be updated. */
public interface SinkSignal {
  /** Informs the signal that a new value is available at {@code timestamp}. */
  void update(Instant timestamp);
}
