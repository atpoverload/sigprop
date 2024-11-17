package sigprop2.signal.concurrent;

import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import sigprop2.SourceSignal;
import sigprop2.signal.SubscribeableSignal;

/** A {@link SourceSignal} that provides concurrent mapping functions. */
public abstract class ConcurrentSubscribeableSignal<T> extends SubscribeableSignal<T> {
  private final Executor executor;

  protected ConcurrentSubscribeableSignal(Executor executor) {
    this.executor = executor;
  }

  /** Creates a signal that maps this signal. */
  @Override
  public final <U> SourceSignal<U> mapFunc(Function<T, U> func) {
    return this.sink(new ConcurrentMappedSignal<>(this, func, executor));
  }

  /** Creates a signal that compoes this signal and another. */
  @Override
  public final <U, V> SourceSignal<V> composeFunc(BiFunction<T, U, V> func, SourceSignal<U> other) {
    return this.compose(
        (me, them) -> new ConcurrentComposedSignal<>(me, them, func, executor), other);
  }
}
