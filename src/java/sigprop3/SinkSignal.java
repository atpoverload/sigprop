package sigprop3;

import java.time.Instant;

/** A signal that can be notified that its value updated. */
public interface SinkSignal {
  /** Informs the signal that a new value is available at the given timestamp. */
  void update(Instant timestamp);
}
