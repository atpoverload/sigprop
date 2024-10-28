package sigprop.signal.async;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.Function;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.signal.SignalAttributionFunction;
import sigprop.signal.SymbolicSignal;

/** A {@link SymbolicSignal} that concurrently updates downstream signals. */
public final class ConcurrentSymbolicSignal<T, U> extends SymbolicSignal<T, U> {
  public static <T, U> Function<SourceSignal<T>, ConcurrentSymbolicSignal<T, U>> fromFunc(
      SignalAttributionFunction<T, T, U> func, Executor executor) {
    return signal -> new ConcurrentSymbolicSignal<>(signal, func, executor);
  }

  private final Executor executor;

  public ConcurrentSymbolicSignal(
      SourceSignal<T> source, SignalAttributionFunction<T, T, U> func, Executor executor) {
    super(source, func);
    this.executor = executor;
  }

  @Override
  protected void updateSignal(Instant timestamp, SinkSignal<?> signal) {
    executor.execute(() -> signal.update(timestamp));
  }
}
