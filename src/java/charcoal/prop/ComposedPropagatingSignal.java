package charcoal.prop;

import charcoal.ComposedSignal;
import charcoal.SinkSignal;
import charcoal.SourceSignal;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public final class ComposedPropagatingSignal<T, U> extends DelegatingSignal<Entry<T, U>>
    implements ComposedSignal<T, U> {
  private final SourceSignal<T> first;
  private final SourceSignal<U> second;

  ComposedPropagatingSignal(SourceSignal<T> first, SourceSignal<U> second, Executor executor) {
    super(executor);
    this.first = first;
    this.second = second;
  }

  @Override
  public Entry<T, U> sample(Instant timestamp) {
    return new SimpleImmutableEntry<>(first.sample(timestamp), second.sample(timestamp));
  }

  /** Creates a sink connected to this signal, which consumes this signal. */
  @Override
  public <S extends SinkSignal> S map(BiFunction<SourceSignal<T>, SourceSignal<U>, S> mapping) {
    return this.map(() -> mapping.apply(first, second));
  }

  /** Creates a sink connected to this signal, which consumes this signal. */
  @Override
  public <S extends SinkSignal> S asyncMap(
      BiFunction<SourceSignal<T>, SourceSignal<U>, S> mapping) {
    return this.asyncMap(() -> mapping.apply(first, second));
  }

  /** Creates a sink connected to this signal, which consumes this signal. */
  public <V> BiMappingSignal<T, U, V> mapBiFunc(BiFunction<T, U, V> mapping) {
    return this.map(() -> new BiFunctionMappingSignal<>(first, second, mapping, this.executor()));
  }

  /** Creates a sink connected to this signal, which consumes this signal. */
  public <V> BiMappingSignal<T, U, V> asyncMapBiFunc(BiFunction<T, U, V> mapping) {
    return this.asyncMap(
        () -> new BiFunctionMappingSignal<>(first, second, mapping, this.executor()));
  }
}
