package sigprop.examples;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import sigprop.SourceSignal;
import sigprop.signal.SymbolicSignal;
import sigprop.signal.async.ConcurrentSymbolicSignal;
import sigprop.signal.async.SampledSource;
import sigprop.signal.sync.SyncSymbolicSignal;
import sigprop.signal.util.SignalLogger;

public class FakeCounter {
  private static final Function<SourceSignal<Integer>, SyncSymbolicSignal<Integer, Double>>
      counterRate = SyncSymbolicSignal.fromFunc(FakeCounter::toCounterRate);

  private static double toCounterRate(Instant start, Instant end, int first, int second) {
    long rate = second - first;
    double elapsed = Duration.between(start, end).toNanos() / 1000000000.0;
    return rate / elapsed;
  }

  private final AtomicInteger counter = new AtomicInteger();

  private final int lower;
  private final int upper;

  private FakeCounter(int upper) {
    this(0, upper);
  }

  private FakeCounter(int lower, int upper) {
    this.lower = lower;
    this.upper = upper;
  }

  private int get() {
    return counter.addAndGet(ThreadLocalRandom.current().nextInt(lower, upper));
  }

  public static void main(String[] args) throws Exception {
    final int periodMillis = 13;
    final AtomicInteger threadCounter = new AtomicInteger(0);
    ScheduledExecutorService executor =
        newScheduledThreadPool(
            2,
            r -> {
              Thread t =
                  new Thread(
                      r, String.format("sigprop-worker-%d", threadCounter.getAndIncrement()));
              t.setDaemon(true);
              return t;
            });
    FakeCounter counter1 = new FakeCounter(0, 4 * periodMillis);
    FakeCounter counter2 = new FakeCounter(periodMillis, 3 * periodMillis);
    Function<SourceSignal<Integer>, ConcurrentSymbolicSignal<Integer, Double>> asyncCounterRate =
        ConcurrentSymbolicSignal.fromFunc(FakeCounter::toCounterRate, executor);

    SymbolicSignal<Integer, Double> source =
        SampledSource.fixedPeriodMillis(counter1::get, periodMillis, executor).map(counterRate);
    source.map(SignalLogger::new);
    source.compose(
        SampledSource.fixedPeriodMillis(counter2::get, periodMillis, executor).map(asyncCounterRate),
        SignalLogger::new);
    int iters = 0;
    while (iters++ == 0) {
      Thread.sleep(1000);
    }
    executor.shutdown();
  }
}
