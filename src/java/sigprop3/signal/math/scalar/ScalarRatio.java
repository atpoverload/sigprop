package sigprop3.signal.math.scalar;

import java.time.Instant;
import sigprop3.SourceSignal;
import sigprop3.signal.sync.SynchronousDelegatingSignal;

/** A {@SynchronousDelegatingSignal} that computes the ratio of two source signals. */
public final class ScalarRatio extends SynchronousDelegatingSignal<Double> {
  private final SourceSignal<? extends Number> first;
  private final SourceSignal<? extends Number> second;

  public ScalarRatio(SourceSignal<? extends Number> first, SourceSignal<? extends Number> second) {
    this.first = first;
    this.second = second;
  }

  /** Returns the ratio of the inputs. */
  @Override
  public Double sample(Instant timestamp) {
    return first.sample(timestamp).doubleValue() / second.sample(timestamp).doubleValue();
  }
}
