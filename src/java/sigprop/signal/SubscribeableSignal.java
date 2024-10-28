package sigprop.signal;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import sigprop.SinkSignal;
import sigprop.SourceSignal;

/** An abstract {@link SourceSignal} that has downstream {@link SinkSignals}. */
public abstract class SubscribeableSignal<T> implements SourceSignal<T> {
  private final ArrayList<SinkSignal<?>> downstream = new ArrayList<>();

  /** Adds a {@link SinkSignal} to the downstream. */
  @Override
  public final <U, S extends SinkSignal<U>> S map(Function<SourceSignal<T>, S> sinkFactory) {
    S sink = sinkFactory.apply(this);
    downstream.add(sink);
    return sink;
  }

  /** Adds a {@link SinkSignal} to the downstream. */
  @Override
  public final <U, V, S extends SinkSignal<V>> S compose(
      SourceSignal<U> other, BiFunction<SourceSignal<T>, SourceSignal<U>, S> sinkFactory) {
    S sink = sinkFactory.apply(this, other);
    this.map(unused -> sink);
    other.map(unused -> sink);
    return sink;
  }

  /** Returns a shallow copy of the downstream {@link SinkSignals}. */
  protected final Iterable<SinkSignal<?>> downstream() {
    return new ArrayList<>(downstream);
  }
}
