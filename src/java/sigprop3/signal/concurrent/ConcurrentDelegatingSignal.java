package sigprop3.signal.concurrent;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop3.SinkSignal;
import sigprop3.signal.DelegatingSignal;

/** A {@link DelegatingSignal} that updates the downstream concurrently. */
public abstract class ConcurrentDelegatingSignal<T> extends DelegatingSignal<T> {
  private final Executor executor;

  protected ConcurrentDelegatingSignal(Executor executor) {
    this.executor = executor;
  }

  /** Informs all downstream signals to update using the given executor. */
  @Override
  protected final <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    executor.execute(() -> signal.update(timestamp));
  }
}
