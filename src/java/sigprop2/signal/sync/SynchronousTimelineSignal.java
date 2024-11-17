package sigprop2.signal.sync;

import java.time.Instant;
import java.util.TreeMap;
import sigprop2.SourceSignal;

/** A {@link ProcessingSignal} that updates downstream when there are at least two data points. */
public abstract class SynchronousTimelineSignal<T, U> extends SynchronousProcessingSignal<U> {
  private final SourceSignal<T> source;

  private final TreeMap<Instant, T> timeline = new TreeMap<>();

  protected SynchronousTimelineSignal(SourceSignal<T> source) {
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

  /** Informs all downstream signals to update if there are at least two updates. */
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
