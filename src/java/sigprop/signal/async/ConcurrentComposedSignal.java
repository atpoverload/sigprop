package sigprop.signal.async;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import sigprop.Signal;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.signal.ComposedSignal;

/** A {@link ComposedSignal} that concurrently updates downstream signals. */
public final class ConcurrentComposedSignal<T, U, V> extends ComposedSignal<T, U, V> {
  public static <T, U, V>
      BiFunction<SourceSignal<T>, SourceSignal<U>, ConcurrentComposedSignal<T, U, V>> fromFunc(
          BiFunction<T, U, V> func, Executor executor) {
    return (signal1, signal2) -> new ConcurrentComposedSignal<>(signal1, signal2, func, executor);
  }

  private final Executor executor;

  public ConcurrentComposedSignal(
      Signal<T> first, Signal<U> second, BiFunction<T, U, V> func, Executor executor) {
    super(first, second, func);
    this.executor = executor;
  }

  @Override
  protected void updateSignal(Instant timestamp, SinkSignal<?> signal) {
    executor.execute(() -> signal.update(timestamp));
  }
}
