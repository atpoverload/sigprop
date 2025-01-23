package sigprop.signal.math.scalar;

import java.time.Instant;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import sigprop.SourceSignal;
import sigprop.signal.MappingSignal;
import sigprop.signal.math.Uncertainty;

public class ScalarErrorPropagator<T extends Number> extends MappingSignal<T, Uncertainty> {
  private final TreeMap<Instant, T> data = new TreeMap<>();

  public ScalarErrorPropagator(SourceSignal<T> source, Executor executor) {
    super(source, executor);
  }

  @Override
  public final Uncertainty compute(Instant timestamp, T value) {
    data.put(timestamp, value);
    Collection<T> history = data.headMap(timestamp).values();
    double n = history.size();
    double mu = history.stream().mapToDouble(v -> v.doubleValue()).average().getAsDouble();
    double sigma = history.stream().mapToDouble(v -> Math.pow(v.doubleValue() - mu, 2)).sum();
    sigma = Math.sqrt(sigma / n);
    return new Uncertainty((int) n, mu, sigma);
  }
}