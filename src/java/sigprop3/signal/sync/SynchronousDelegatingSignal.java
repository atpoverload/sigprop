package sigprop3.signal.sync;

import java.time.Instant;
import sigprop3.SinkSignal;
import sigprop3.signal.DelegatingSignal;

/** A {@link DelegatingSignal} that updates the downstream synchronously. */
public abstract class SynchronousDelegatingSignal<T> extends DelegatingSignal<T> {
  /** Directly calls update for the signal. */
  @Override
  protected final <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    signal.update(timestamp);
  }
}
