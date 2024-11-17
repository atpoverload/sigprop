package sigprop3.signal.sync;

import java.time.Instant;
import sigprop3.SinkSignal;
import sigprop3.signal.SubscribeableSignal;

/** A {@link SubscribeableSignal} that updates the downstream synchronously. */
public abstract class SynchronousSignal<T> extends SubscribeableSignal<T> {
  /** Directly calls update for the signal. */
  @Override
  protected final <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    signal.update(timestamp);
  }
}
