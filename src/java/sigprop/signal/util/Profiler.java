package sigprop.signal.util;

import java.time.Instant;
import java.util.NavigableSet;
import sigprop.SinkSignal;

/** A {@link SinkSignal} that has a searchable timeline. */
public interface Profiler<T> extends SinkSignal<T> {
  /** Returns all instants that have been updated in this profiler. */
  NavigableSet<Instant> timeline();
}
