package sigprop3.signal.sync;

import java.time.Instant;
import sigprop3.SinkSignal;
import sigprop3.SourceSignal;
import sigprop3.signal.TimelineSignal;

/** A {@link TimelineSignal} that updates the downstream synchronously. */
public abstract class SynchronousTimelineSignal<T, U> extends TimelineSignal<T, U> {
  protected SynchronousTimelineSignal(SourceSignal<T> source) {
    super(source);
  }

  /** Directly calls update for the signal. */
  @Override
  protected final <S extends SinkSignal> void updateSignal(S signal, Instant timestamp) {
    signal.update(timestamp);
  }
}
