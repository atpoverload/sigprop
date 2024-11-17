package sigprop3.signal.math;

import java.time.Instant;
import java.util.Collection;
import java.util.TreeMap;
import sigprop3.SourceSignal;
import sigprop3.signal.sync.SynchronousDelegatingSignal;

public class ErrorPropagator extends SynchronousDelegatingSignal<Uncertainty> {
  private final SourceSignal<? extends Number> source;

  private final TreeMap<Instant, Number> values = new TreeMap<>();

  public ErrorPropagator(SourceSignal<? extends Number> source) {
    this.source = source;
  }

  @Override
  public final Uncertainty sample(Instant timestamp) {
    values.put(timestamp, (Number) source.sample(timestamp));
    Collection<Number> history = values.headMap(timestamp).values();
    double n = history.size();
    double mu = history.stream().mapToDouble(v -> v.doubleValue()).average().getAsDouble();
    double sigma = history.stream().mapToDouble(v -> Math.pow(v.doubleValue() - mu, 2)).sum();
    sigma = Math.sqrt(sigma / n);
    return new Uncertainty((int) n, mu, sigma);
  }
}
