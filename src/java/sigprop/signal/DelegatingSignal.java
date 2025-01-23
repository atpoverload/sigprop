package sigprop.signal;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop.SinkSignal;

public abstract class DelegatingSignal<T> extends PropagatingSignal<T> implements SinkSignal {
  protected DelegatingSignal(Executor executor) {
    super(executor);
  }

  /** Informs all downstream signals to update if there are at least two updates. */
  @Override
  public final void update(Instant timestamp) {
    propagate(timestamp);
  }
}