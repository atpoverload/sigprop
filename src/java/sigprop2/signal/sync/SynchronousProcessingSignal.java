package sigprop2.signal.sync;

import java.time.Instant;
import sigprop2.SinkSignal;

/** A {@link SynchronousSubscribeableSignal} that also updates all downstream signals. */
public abstract class SynchronousProcessingSignal<T> extends SynchronousSubscribeableSignal<T>
    implements SinkSignal {
  /** Informs all downstream signals to update. */
  @Override
  public void update(Instant timestamp) {
    downstream().forEach(signal -> signal.update(timestamp));
  }
}
