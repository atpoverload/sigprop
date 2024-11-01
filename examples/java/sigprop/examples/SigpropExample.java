package sigprop.examples;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import sigprop2.SourceSignal;
import sigprop2.signal.TimelineSignal;
import sigprop2.signal.concurrent.ConcurrentSubscribeableSignal;
import sigprop2.util.LoggerSink;

public class SigpropExample {
  private static final ScheduledExecutorService EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "sigprop-counter");
            t.setDaemon(true);
            return t;
          });

  private static class Counter extends ConcurrentSubscribeableSignal<Integer> {
    private final AtomicInteger counter = new AtomicInteger(0);

    private Counter() {
      super(EXECUTOR);
      EXECUTOR.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public final Integer sample(Instant timestamp) {
      return counter.get();
    }

    public void tick() {
      final Instant timestamp = Instant.now();
      counter.addAndGet(ThreadLocalRandom.current().nextInt(1, 5));
      downstream().forEach(signal -> signal.update(timestamp));
    }
  }

  private static class CounterRate extends TimelineSignal<Integer, Double> {
    private CounterRate(SourceSignal<Integer> source) {
      super(source);
    }

    @Override
    protected Double defaultSignalValue() {
      return 0.0;
    }

    @Override
    protected Double computeSignalValue(Instant start, Instant end, Integer first, Integer second) {
      double flux = second - first;
      double elapsed = ((double) Duration.between(start, end).toNanos()) / 1000000000.0;
      return flux / elapsed;
    }
  }

  public static void main(String[] args) throws Exception {
    final Function<Double, String> double_formatter = d -> String.format("%4.4f", d);

    new Counter()
        .map(CounterRate::new)
        .composeFunc(new Counter().map(CounterRate::new), (c1, c2) -> c1 / c2)
        .composeFunc(new Counter().map(CounterRate::new), (c1, c2) -> c1 * c2)
        .mapFunc(double_formatter)
        .map(LoggerSink::new);

    while (true) {
      // signal.tick();
      Thread.sleep(100);
    }
  }
}
