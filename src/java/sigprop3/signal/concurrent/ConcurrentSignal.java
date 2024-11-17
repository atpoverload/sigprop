package sigprop3.signal.concurrent;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop3.SinkSignal;
import sigprop3.signal.SubscribeableSignal;

/** A {@link SubscribeableSignal} that updates the downstream concurrently. */
public abstract class ConcurrentSignal<T> extends SubscribeableSignal<T> {
  private final Executor executor;

  protected ConcurrentSignal(Executor executor) {
    this.executor = executor;
  }

  /** Informs all downstream signals to update using the given executor. */
  @Override
  protected final <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    executor.execute(() -> signal.update(timestamp));
  }
}
