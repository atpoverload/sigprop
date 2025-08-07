package charcoal.prop;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public final class ButtonSignal extends PropagatingSignal<Instant> {
  private final Supplier<Instant> timeSource;

  public ButtonSignal(Supplier<Instant> timeSource, ScheduledExecutorService executor) {
    super(executor);
    this.timeSource = timeSource;
  }

  /** Returns the timestamp closest to the given one. */
  @Override
  public final Instant sample(Instant timestamp) {
    // TODO: this is a hack. it'll be fixed when you can't sample propagating signals be default
    return timeSource.get();
  }
}
