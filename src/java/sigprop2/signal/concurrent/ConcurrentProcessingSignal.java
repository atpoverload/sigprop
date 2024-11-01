package sigprop2.signal.concurrent;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop2.SinkSignal;

/** A {@link ConcurrentSubscribeableSignal} that also updates all downstream signals. */
public abstract class ConcurrentProcessingSignal<T> extends ConcurrentSubscribeableSignal<T>
    implements SinkSignal {
  private final Executor executor;

  protected ConcurrentProcessingSignal(Executor executor) {
    super(executor);
    this.executor = executor;
  }

  /** Informs all downstream signals to update. */
  @Override
  public void update(Instant timestamp) {
    downstream().forEach(signal -> executor.execute(() -> signal.update(timestamp)));
  }
}
