package sigprop3.signal.concurrent;

import java.time.Instant;
import java.util.concurrent.Executor;
import sigprop3.SinkSignal;
import sigprop3.SourceSignal;
import sigprop3.signal.TimelineSignal;

/** A {@link TimelineSignal} that updates the downstream concurrently. */
public abstract class ConcurrentTimelineSignal<T, U> extends TimelineSignal<T, U> {
  private final Executor executor;

  protected ConcurrentTimelineSignal(SourceSignal<T> source, Executor executor) {
    super(source);
    this.executor = executor;
  }

  /** Informs all downstream signals to update using the given executor. */
  @Override
  protected final <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    executor.execute(() -> signal.update(timestamp));
  }
}
