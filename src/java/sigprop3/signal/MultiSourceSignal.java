package sigprop3.signal;

import java.time.Instant;

public interface MultiSourceSignal {
  /** Returns the value of the signal at the given {@link Instant} of a specific type. */
  <U> U sampleAs(Instant timestamp, Class<U> cls);
}
