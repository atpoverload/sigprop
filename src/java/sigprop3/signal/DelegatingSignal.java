package sigprop3.signal;

import java.time.Instant;
import sigprop3.SinkSignal;

/** A {@link SubscribeableSignal} that updates its downstream signals. */
public abstract class DelegatingSignal<T> extends SubscribeableSignal<T> implements SinkSignal {
  /** Informs all downstream signals to update. */
  @Override
  public final void update(Instant timestamp) {
    updateDownstream(timestamp);
  }
}
