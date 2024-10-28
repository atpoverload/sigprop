package sigprop.signal.sync;

import java.time.Instant;
import java.util.function.BiFunction;
import sigprop.Signal;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.signal.ComposedSignal;

/** A {@link ComposedSignal} that synchronous updates downstream signals. */
public final class SyncComposedSignal<T, U, V> extends ComposedSignal<T, U, V> {
  public static <T, U, V>
      BiFunction<SourceSignal<T>, SourceSignal<U>, SyncComposedSignal<T, U, V>> fromFunc(
          BiFunction<T, U, V> func) {
    return (signal1, signal2) -> new SyncComposedSignal<>(signal1, signal2, func);
  }

  public SyncComposedSignal(Signal<T> first, Signal<U> second, BiFunction<T, U, V> func) {
    super(first, second, func);
  }

  @Override
  protected void updateSignal(Instant timestamp, SinkSignal<?> signal) {
    signal.update(timestamp);
  }
}
