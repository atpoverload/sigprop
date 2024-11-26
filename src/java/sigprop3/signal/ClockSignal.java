package sigprop3.signal;

import java.time.Instant;

/** A {@link SubscribeableSignal}that {@code ticks} periodically to update its downstream. */
public interface ClockSignal {
  /** The last time this clock "ticked". */
  Instant lastTick();

  /** Starts the clock. */
  void start();

  /** Stops the clock. */
  void stop();
}
