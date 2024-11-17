package sigprop3.signal.concurrent;

import java.util.concurrent.Executor;
import sigprop3.SinkSignal;

/** A {@link ConcurrentSignal} that is a sink. This exists for user convenience. */
public abstract class ConcurrentProcessingSignal<T> extends ConcurrentSignal<T>
    implements SinkSignal {
  protected ConcurrentProcessingSignal(Executor executor) {
    super(executor);
  }
}
