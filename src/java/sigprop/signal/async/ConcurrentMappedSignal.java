package sigprop.signal.async;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.Function;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.signal.MappedSignal;

/** A {@link MappedSignal} that synchronous updates downstream signals. */
public final class ConcurrentMappedSignal<T, U> extends MappedSignal<T, U> {
  public static <T, U> Function<SourceSignal<T>, ConcurrentMappedSignal<T, U>> fromFunc(
      Function<T, U> func, Executor executor) {
    return signal -> new ConcurrentMappedSignal<>(signal, func, executor);
  }

  private final Executor executor;

  public ConcurrentMappedSignal(SourceSignal<T> source, Function<T, U> func, Executor executor) {
    super(source, func);
    this.executor = executor;
  }

  @Override
  protected void updateSignal(Instant timestamp, SinkSignal<?> signal) {
    executor.execute(() -> signal.update(timestamp));
  }
}
