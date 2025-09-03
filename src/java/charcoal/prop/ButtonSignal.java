package charcoal.prop;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;

/* A propagating signal that  to trigger other signals */
public final class ButtonSignal extends PropagatingSignal<Void> {
  public ButtonSignal(ScheduledExecutorService executor) {
    super(executor);
  }

  /** Buttons can't be sampled, so it returns nothing. */
  @Override
  public final Void sample(Instant timestamp) {
    return null;
  }
}
