package sigprop;

import java.time.Instant;

/**
 * An interface that contains a continuous signal. Although Java is restricted to nanosecond
 * precision, the notion of a continuous timeline is physical property.
 */
public interface Signal<T> {
  /** Returns the value of the signal at the given {@link Instant}. */
  // TODO: this could consume a "functional"; see
  // https://en.wikipedia.org/wiki/Functional_(mathematics)
  T sample(Instant timestamp);
}
