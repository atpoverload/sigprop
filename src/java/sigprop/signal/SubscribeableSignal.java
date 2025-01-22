package sigprop.signal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import sigprop.AsyncComposableSignal;
import sigprop.AsyncMappableSignal;
import sigprop.ComposableSignal;
import sigprop.MappableSignal;
import sigprop.SinkSignal;

/** A signal that has a downstream of signals it updates. */
public abstract class SubscribeableSignal<T>
    implements AsyncComposableSignal<T>,
        AsyncMappableSignal<T>,
        ComposableSignal<T>,
        MappableSignal<T> {
  private final Executor executor;

  private final ArrayList<SinkSignal> syncDownstream = new ArrayList<>();
  private final ArrayList<SinkSignal> asyncDownstream = new ArrayList<>();

  protected SubscribeableSignal(Executor executor) {
    this.executor = executor;
  }

  /** Creates a new {@link SinkSignal} to add to the downstream. */
  @Override
  public final <S extends SinkSignal> S map(Supplier<S> sinkFactory) {
    S sink = sinkFactory.get();
    syncDownstream.add(sink);
    return sink;
  }

  /** Creates a {@link SinkSignal} using this signal to add to the downstream. */
  @Override
  public final <S extends SinkSignal> S map(Function<MappableSignal<T>, S> mapping) {
    return this.map(() -> mapping.apply(this));
  }

  /** Creates a new {@link SinkSignal} to add to the downstream. */
  @Override
  public final <S extends SinkSignal> S asyncMap(Supplier<S> sinkFactory) {
    S sink = sinkFactory.get();
    asyncDownstream.add(sink);
    return sink;
  }

  /** Creates a {@link SinkSignal} using this signal to add to the downstream. */
  @Override
  public final <S extends SinkSignal> S asyncMap(Function<AsyncMappableSignal<T>, S> mapping) {
    return this.asyncMap(() -> mapping.apply(this));
  }

  /**
   * Creates a {@link SinkSignal} using this signal and another composable signal to add to the
   * downstream.
   */
  @Override
  public final <U, S extends SinkSignal> S compose(
      BiFunction<ComposableSignal<T>, ComposableSignal<U>, S> composition,
      ComposableSignal<U> other) {
    final S sink = composition.apply(this, other);
    this.map(() -> sink);
    other.map(() -> sink);
    return sink;
  }

  /**
   * Creates a {@link SinkSignal} using this signal and another composable signal to add to the
   * downstream.
   */
  @Override
  public final <U, S extends SinkSignal> S asyncCompose(
      BiFunction<AsyncComposableSignal<T>, AsyncComposableSignal<U>, S> composition,
      AsyncComposableSignal<U> other) {
    final S sink = composition.apply(this, other);
    this.asyncMap(() -> sink);
    other.asyncMap(() -> sink);
    return sink;
  }

  /** Updates all downstream signals. */
  protected final void updateDownstream(Instant now) {
    asyncDownstream.forEach(signal -> executor.execute(() -> signal.update(now)));
    syncDownstream.forEach(signal -> signal.update(now));
  }
}
