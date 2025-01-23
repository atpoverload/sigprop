package sigprop.signal;

import java.time.Instant;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/** A {@link SubscribeableSignal} that updates the downstream when {@code pushed}. */
public final class ButtonSignal extends PropagatingSignal<Instant> {
  private final Supplier<Instant> timeSource;

  private final TreeSet<Instant> timeline = new TreeSet<>();

  public ButtonSignal(Supplier<Instant> timeSource, Executor executor) {
    super(executor);
    this.timeSource = timeSource;
  }

  /** Returns the timestamp closest to the given one. */
  @Override
  public final Instant sample(Instant timestamp) {
    return timeline.headSet(timestamp).last();
  }

  /** Update all downstream signals. */
  public void push() {
    Instant now = timeSource.get();
    synchronized (timeline) {
      timeline.add(now);
    }
    propagate(now);
  }
}
