package sigprop.signal;

import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import sigprop.SinkSignal;
import sigprop.SourceSignal;

/**
 * A {@link PropagatingSignal} that updates its downstream when there is more the one data point.
 */
public abstract class TimelineSignal<T, U> extends PropagatingSignal<U> implements SinkSignal {
  private final SourceSignal<T> source;

  private final TreeMap<Instant, T> timeline = new TreeMap<>();

  protected TimelineSignal(SourceSignal<T> source, Executor executor) {
    super(executor);
    this.source = source;
  }

  /**
   * Returns a computation done over an interval, i.e., the region of time between two events of
   * some type. Users should implement the {@code defaultSignal} and {@code computeSignal} methods.
   */
  @Override
  public final U sample(Instant timestamp) {
    Instant end = timeline.floorKey(timestamp);
    Instant start = end;
    while (start.equals(end)) {
      if (!start.equals(timeline.firstKey())) {
        start = timeline.headMap(start, false).lastKey();
      } else {
        end = timeline.tailMap(end, false).firstKey();
      }
    }

    T first = timeline.get(start);
    T second = timeline.get(end);

    return compute(start, end, first, second);
  }

  /** Informs all downstream signals to update if there are at least two updates. */
  @Override
  public final void update(Instant timestamp) {
    timeline.put(timestamp, source.sample(timestamp));
    if (timeline.size() > 1) {
      propagate(timestamp);
    }
  }

  /** Computes the signal value over the given interval. */
  protected abstract U compute(Instant start, Instant end, T first, T second);
}
