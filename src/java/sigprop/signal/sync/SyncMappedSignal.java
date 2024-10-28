package sigprop.signal.sync;

import java.time.Instant;
import java.util.function.Function;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.signal.MappedSignal;

/** A {@link MappedSignal} that synchronous updates downstream signals. */
public final class SyncMappedSignal<T, U> extends MappedSignal<T, U> {
  public static <T, U> Function<SourceSignal<T>, SyncMappedSignal<T, U>> fromFunc(
      Function<T, U> func) {
    return signal -> new SyncMappedSignal<>(signal, func);
  }

  public SyncMappedSignal(SourceSignal<T> source, Function<T, U> func) {
    super(source, func);
  }

  @Override
  protected void updateSignal(Instant timestamp, SinkSignal<?> signal) {
    signal.update(timestamp);
  }
}
