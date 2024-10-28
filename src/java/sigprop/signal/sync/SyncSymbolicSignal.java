package sigprop.signal.sync;

import java.time.Instant;
import java.util.function.Function;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.signal.SignalAttributionFunction;
import sigprop.signal.SymbolicSignal;

/** A {@link SymbolicSignal} that synchronously updates downstream signals. */
public final class SyncSymbolicSignal<T, U> extends SymbolicSignal<T, U> {
  public static <T, U> Function<SourceSignal<T>, SyncSymbolicSignal<T, U>> fromFunc(
      SignalAttributionFunction<T, T, U> func) {
    return signal -> new SyncSymbolicSignal<>(signal, func);
  }

  public SyncSymbolicSignal(SourceSignal<T> source, SignalAttributionFunction<T, T, U> func) {
    super(source, func);
  }

  @Override
  protected void updateSignal(Instant timestamp, SinkSignal<?> signal) {
    signal.update(timestamp);
  }
}
