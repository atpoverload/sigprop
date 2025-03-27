package charcoal.prop;

import charcoal.ComposableSignal;
import charcoal.MappableSignal;
import charcoal.SinkSignal;
import charcoal.SourceSignal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

/** A signal that has a downstream of signals it updates. */
public abstract class PropagatingSignal<T>
    implements ComposableSignal<T>, MappableSignal<T>, SourceSignal<T> {
  private final ArrayList<SinkSignal> syncDownstream = new ArrayList<>();
  private final ArrayList<SinkSignal> asyncDownstream = new ArrayList<>();

  private final Executor executor;

  protected PropagatingSignal(Executor executor) {
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
  public final <S extends SinkSignal> S map(Function<SourceSignal<T>, S> mapping) {
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
  public final <S extends SinkSignal> S asyncMap(Function<SourceSignal<T>, S> mapping) {
    return this.asyncMap(() -> mapping.apply(this));
  }

  /**
   * Creates a {@link SinkSignal} using this signal and another composable signal to add to the
   * downstream.
   */
  @Override
  public final <U> ComposedPropagatingSignal<T, U> compose(SourceSignal<U> other) {
    ComposedPropagatingSignal<T, U> sink =
        new ComposedPropagatingSignal<>(this, other, this.executor);
    this.map(() -> sink);
    try {
      ((MappableSignal<?>) other).map(() -> sink);
    } catch (Exception e) {
      // Silently ignoring for now.
      e.printStackTrace();
    }
    return sink;
  }

  /**
   * Creates a {@link SinkSignal} using this signal and another composable signal to add to the
   * downstream.
   */
  @Override
  public final <U> ComposedPropagatingSignal<T, U> asyncCompose(SourceSignal<U> other) {
    ComposedPropagatingSignal<T, U> sink =
        new ComposedPropagatingSignal<>(this, other, this.executor);
    this.asyncMap(() -> sink);
    try {
      ((MappableSignal<?>) other).asyncMap(() -> sink);
    } catch (Exception e) {
      // Silently ignoring for now.
      e.printStackTrace();
    }
    return sink;
  }

  /** Creates a {@link SinkSignal} using this signal to add to the downstream. */
  public final <U> MappingSignal<T, U> mapFunc(Function<T, U> mapping) {
    return this.map(() -> new FunctionMappingSignal<>(this, mapping, this.executor));
  }

  /** Creates a {@link SinkSignal} using this signal to add to the downstream. */
  public final <U> MappingSignal<T, U> asyncMapFunc(Function<T, U> mapping) {
    return this.asyncMap(() -> new FunctionMappingSignal<>(this, mapping, this.executor));
  }

  /** Updates all downstream signals. */
  public final void propagate(Instant now) {
    asyncDownstream.forEach(signal -> executor.execute(() -> signal.update(now)));
    syncDownstream.forEach(signal -> signal.update(now));
  }

  protected Executor executor() {
    return this.executor;
  }
}
