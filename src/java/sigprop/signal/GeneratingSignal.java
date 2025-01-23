package sigprop.signal;

import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import sigprop.SinkSignal;

public abstract class GeneratingSignal<T> extends PropagatingSignal<T> implements SinkSignal {
  private final TreeMap<Instant, T> timeline = new TreeMap<>();

  protected GeneratingSignal(Executor executor) {
    super(executor);
  }

  /**
   * Returns a computation done over an interval, i.e., the region of time between two events of
   * some type. Users should implement the {@code defaultSignal} and {@code computeSignal} methods.
   */
  @Override
  public final T sample(Instant timestamp) {
    return timeline.headMap(timestamp, true).lastEntry().getValue();
  }

  /** Informs all downstream signals to update if there are at least two updates. */
  @Override
  public final void update(Instant timestamp) {
    Instant now = Instant.now();
    timeline.put(now, generate());
    propagate(timestamp);
  }

  /** Computes the signal value over the given interval. */
  protected abstract T generate();
}
