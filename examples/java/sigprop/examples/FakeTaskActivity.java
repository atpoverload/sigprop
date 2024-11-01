package sigprop.examples;

import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import sigprop2.SourceSignal;
import sigprop2.signal.SubscribeableSignal;
import sigprop2.signal.TimelineSignal;
import sigprop2.util.LoggerSink;

public final class FakeTaskActivity {
  private static class TaskManager extends SubscribeableSignal<Map<Integer, Integer>> {
    private final HashMap<Integer, AtomicInteger> tasks = new HashMap<>();

    @Override
    public final Map<Integer, Integer> sample(Instant timestamp) {
      return tasks.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().get()));
    }

    private void tick() {
      final Instant timestamp = Instant.now();
      tasks.forEach((tid, counter) -> counter.addAndGet(ThreadLocalRandom.current().nextInt(1, 5)));
      downstream().forEach(signal -> signal.update(timestamp));
    }

    private void addProcess(int taskId) {
      tasks.computeIfAbsent(taskId, tid -> new AtomicInteger(0));
    }

    private void removeProcess(int taskId) {
      tasks.remove(taskId);
    }
  }

  private static class FakeSystem extends SubscribeableSignal<Integer> {
    private final TaskManager taskManager = new TaskManager();
    private final AtomicInteger totalCounts = new AtomicInteger(0);

    @Override
    public final Integer sample(Instant timestamp) {
      return totalCounts.get();
    }

    private void tick() {
      final Instant timestamp = Instant.now();
      taskManager.tick();
      totalCounts.addAndGet(
          taskManager.sample(timestamp).values().stream().mapToInt(Integer::intValue).sum());
      downstream().forEach(signal -> signal.update(timestamp));
    }
  }

  private static class SingleCounterRate extends TimelineSignal<Integer, Double> {
    private SingleCounterRate(SourceSignal<Integer> source) {
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

  private static class MultiCounterRate
      extends TimelineSignal<Map<Integer, Integer>, Map<Integer, Double>> {
    private MultiCounterRate(SourceSignal<Map<Integer, Integer>> source) {
      super(source);
    }

    @Override
    protected Map<Integer, Double> defaultSignalValue() {
      return new HashMap<>();
    }

    @Override
    protected HashMap<Integer, Double> computeSignalValue(
        Instant start, Instant end, Map<Integer, Integer> first, Map<Integer, Integer> second) {
      HashMap<Integer, Double> fluxes = new HashMap<>();
      double elapsed = ((double) Duration.between(start, end).toNanos()) / 1000000000.0;
      for (int tid : second.keySet()) {
        if (!first.containsKey(tid)) {
          fluxes.put(tid, ((double) second.get(tid)) / elapsed);
        } else {
          fluxes.put(tid, ((double) (second.get(tid) - first.get(tid))) / elapsed);
        }
      }
      return fluxes;
    }
  }

  public static void main(String[] args) throws Exception {
    final AtomicInteger counter = new AtomicInteger();
    FakeSystem system = new FakeSystem();

    SingleCounterRate systemRate = system.map(SingleCounterRate::new);
    systemRate.map(LoggerSink::new);
    systemRate
        .composeFunc(
            system.taskManager.map(MultiCounterRate::new),
            (s, ts) ->
                ts.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue() / s)))
        .map(LoggerSink::new);

    system.taskManager.addProcess(0);

    Random random = new Random();
    while (true) {
      system.tick();
      Thread.sleep(250);
      if (random.nextBoolean()) {
        system.taskManager.addProcess(counter.getAndIncrement());
      }
      if (!system.taskManager.tasks.isEmpty() && random.nextBoolean()) {
        ArrayList<Integer> keys = new ArrayList<>(system.taskManager.tasks.keySet());
        system.taskManager.removeProcess(keys.get(random.nextInt(keys.size())));
      }
    }
  }
}
