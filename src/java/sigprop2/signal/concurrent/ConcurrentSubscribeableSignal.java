package sigprop2.signal.concurrent;

import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import sigprop2.SourceSignal;

/** A {@link SourceSignal} that provides concurrent mapping functions. */
public abstract class ConcurrentSubscribeableSignal<T> extends SourceSignal<T> {
  private final Executor executor;

  protected ConcurrentSubscribeableSignal(Executor executor) {
    this.executor = executor;
  }

  /** Creates a signal that maps this signal. */
  @Override
  public final <U> SourceSignal<U> mapFunc(Function<T, U> func) {
    return this.map(me -> new ConcurrentMappedSignal<>(me, func, executor));
  }

  /** Creates a signal that compoes this signal and another. */
  @Override
  public final <U, V> SourceSignal<V> composeFunc(SourceSignal<U> other, BiFunction<T, U, V> func) {
    return this.compose(
        other, (me, them) -> new ConcurrentComposedSignal<>(me, them, func, executor));
  }
}
