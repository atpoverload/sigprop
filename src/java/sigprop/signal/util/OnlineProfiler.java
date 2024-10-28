package sigprop.signal.util;

import java.time.Instant;
import java.util.NavigableSet;
import java.util.TreeMap;
import sigprop.SourceSignal;

/** A {@link Profiler} that samples data from the source when updated. */
public class OnlineProfiler<T> implements Profiler<T> {
  private final SourceSignal<T> source;
  private final TreeMap<Instant, T> data = new TreeMap<>();

  public OnlineProfiler(SourceSignal<T> source) {
    this.source = source;
  }

  @Override
  public T sample(Instant timestamp) {
    return data.floorEntry(timestamp).getValue();
  }

  @Override
  public void update(Instant timestamp) {
    data.put(timestamp, source.sample(timestamp));
  }

  public NavigableSet<Instant> timeline() {
    return data.navigableKeySet();
  }
}
