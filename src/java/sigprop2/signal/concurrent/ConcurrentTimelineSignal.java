package sigprop2.signal.concurrent;

import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import sigprop2.SourceSignal;

/**
 * A {@link ConcurrentProcessingSignal} that updates downstream when there are at least two data
 * points.
 */
public abstract class ConcurrentTimelineSignal<T, U> extends ConcurrentProcessingSignal<U> {
  private final SourceSignal<T> source;

  private final TreeMap<Instant, T> timeline = new TreeMap<>();

  protected ConcurrentTimelineSignal(SourceSignal<T> source, Executor executor) {
    super(executor);
    this.source = source;
  }

  @Override
  public final U sample(Instant timestamp) {
    if (timeline.size() == 1) {
      return defaultSignalValue();
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

    return computeSignalValue(start, end, first, second);
  }

  /** Informs all downstream signals to update. */
  @Override
  public final void update(Instant timestamp) {
    timeline.put(timestamp, source.sample(timestamp));
    if (timeline.size() > 1) {
      super.update(timestamp);
    }
  }

  protected abstract U defaultSignalValue();

  protected abstract U computeSignalValue(Instant start, Instant end, T first, T second);
}
