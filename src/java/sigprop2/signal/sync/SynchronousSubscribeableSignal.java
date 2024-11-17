package sigprop2.signal.sync;

import java.util.function.BiFunction;
import java.util.function.Function;
import sigprop2.SinkSignal;
import sigprop2.SourceSignal;
import sigprop2.signal.SubscribeableSignal;

/** A {@link SourceSignal} that provides synchronous mapping functions. */
public abstract class SynchronousSubscribeableSignal<T> extends SubscribeableSignal<T> {
  /** Adds a {@link SinkSignal} to the downstream. */
  @Override
  public final <U> SourceSignal<U> mapFunc(Function<T, U> func) {
    return this.sink(new SynchronousMappedSignal<T, U>(this, func));
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  @Override
  public final <U, V> SourceSignal<V> composeFunc(BiFunction<T, U, V> func, SourceSignal<U> other) {
    return this.compose((me, them) -> new SynchronousComposedSignal<>(me, them, func), other);
  }
}
