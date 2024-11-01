package sigprop2.signal;

import java.time.Instant;
import sigprop2.SinkSignal;

/** A {@link SubscribeableSignal} that also updates all downstream signals. */
public abstract class ProcessingSignal<T> extends SubscribeableSignal<T> implements SinkSignal {
  /** Informs all downstream signals to update. */
  @Override
  public void update(Instant timestamp) {
    downstream().forEach(signal -> signal.update(timestamp));
  }
}
