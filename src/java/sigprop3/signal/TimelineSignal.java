package sigprop3.signal;

import java.time.Instant;
import java.util.TreeMap;
import sigprop3.SinkSignal;
import sigprop3.SourceSignal;

/**
 * A {@link SubscribeableSignal} that updates its downstream when there is more the one data point.
 */
public abstract class TimelineSignal<T, U> extends SubscribeableSignal<U> implements SinkSignal {
  private final SourceSignal<T> source;

  private final TreeMap<Instant, T> timeline = new TreeMap<>();

  protected TimelineSignal(SourceSignal<T> source) {
    this.source = source;
  }

  /**
   * Returns a computation done over an interval, i.e., the region of time between two events of
   * some type. Users should implement the {@code defaultSignal} and {@code computeSignal} methods.
   */
  @Override
  public final U sample(Instant timestamp) {
    if (timeline.size() <= 1) {
      return defaultSignal();
    }

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

    return computeSignal(start, end, first, second);
  }

  /** Informs all downstream signals to update if there are at least two updates. */
  @Override
  public final void update(Instant timestamp) {
    timeline.put(timestamp, source.sample(timestamp));
    if (timeline.size() > 1) {
      updateDownstream(timestamp);
    }
  }

  /** Returns the signal value if no data is available. */
  protected abstract U defaultSignal();

  /** Computes the signal value over the given interval. */
  protected abstract U computeSignal(Instant start, Instant end, T first, T second);
}
