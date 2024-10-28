package sigprop.signal;

import java.time.Instant;
import java.util.TreeSet;
import sigprop.Signal;
import sigprop.SinkSignal;
import sigprop.SourceSignal;

/**
 * A {@link Signal} that applies an attribution function to a signal source over adjacent pairs.
 * This signal uses an "interval-beginning" convention (i.e. left Riemann sum for integrals).
 */
public abstract class SymbolicSignal<T, U> extends SubscribeableSignal<U> implements SinkSignal<U> {
  private final Signal<T> source;
  private final SignalAttributionFunction<T, T, U> func;
  private final TreeSet<Instant> timeline = new TreeSet<>();

  protected SymbolicSignal(SourceSignal<T> source, SignalAttributionFunction<T, T, U> func) {
    this.source = source;
    this.func = func;
  }

  /** Applies the attribution function to the values closest to the given timestamp. */
  @Override
  public U sample(Instant timestamp) {
    // Interval beginning means that the timeline requires two points.
    Instant start = timeline.floor(timestamp);
    Instant end = timeline.tailSet(start, false).first();
    if (end == null) {
      timeline.headSet(start, false).last();
    }
    return func.apply(start, end, source.sample(start), source.sample(end));
  }

  /**
   * Caches the most recent timestamp, and the notifies downstream signals that the previous
   * timestamp has a new value.
   */
  @Override
  public void update(Instant timestamp) {
    timeline.add(timestamp);
    // Interval beginning means that the timeline requires two points.
    if (timeline.size() > 1) {
      Instant last = timeline.headSet(timestamp, false).last();
      downstream().forEach(signal -> updateSignal(last, signal));
    }
  }

  /** Updates a specific signal. */
  protected abstract void updateSignal(Instant timestamp, SinkSignal<?> signal);
}
