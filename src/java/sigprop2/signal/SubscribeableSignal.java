package sigprop2.signal;

import java.util.function.BiFunction;
import java.util.function.Function;
import sigprop2.SinkSignal;
import sigprop2.SourceSignal;

/** A {@link SourceSignal} that provides synchronous mapping functions. */
public abstract class SubscribeableSignal<T> extends SourceSignal<T> {
  /** Adds a {@link SinkSignal} to the downstream. */
  @Override
  public final <U> SourceSignal<U> mapFunc(Function<T, U> func) {
    return this.map(me -> new MappedSignal<T, U>(this, func));
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  @Override
  public final <U, V> SourceSignal<V> composeFunc(SourceSignal<U> other, BiFunction<T, U, V> func) {
    return this.compose(other, (me, them) -> new ComposedSignal<>(me, them, func));
  }
}
