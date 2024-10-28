package sigprop.signal;

import java.time.Instant;

/** An interface that applies a function between two values. */
public interface SignalAttributionFunction<T, U, V> {
  /** Applies the attribution function to two timestamps and two values associated with them. */
  V apply(Instant start, Instant end, T first, U second);
}
