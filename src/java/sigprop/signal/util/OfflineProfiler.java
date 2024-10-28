package sigprop.signal.util;

import java.time.Instant;
import java.util.NavigableSet;
import java.util.TreeSet;
import sigprop.SourceSignal;

/** A {@link Profiler} that samples data from the source when sampled. */
public class OfflineProfiler<T> implements Profiler<T> {
  private final SourceSignal<T> source;
  private final TreeSet<Instant> timeline = new TreeSet<>();

  public OfflineProfiler(SourceSignal<T> source) {
    this.source = source;
  }

  @Override
  public T sample(Instant timestamp) {
    return source.sample(timestamp);
  }

  @Override
  public void update(Instant timestamp) {
    timeline.add(timestamp);
  }

  public NavigableSet<Instant> timeline() {
    return new TreeSet<>(timeline);
  }
}
