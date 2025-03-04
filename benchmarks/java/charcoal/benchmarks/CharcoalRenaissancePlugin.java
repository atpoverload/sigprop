package charcoal.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import charcoal.profiler.CharcoalProfiler;
import charcoal.profiler.linux.TaskPower;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import org.renaissance.Plugin;

public final class CharcoalRenaissancePlugin
    implements Plugin.BeforeBenchmarkTearDownListener,
        Plugin.AfterOperationSetUpListener,
        Plugin.BeforeOperationTearDownListener {
  private static final Logger logger = getLogger();

  private static final ScheduledExecutorService SAMPLING_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "charcoal-sampling-thread");
            t.setDaemon(true);
            return t;
          });

  private static final ScheduledExecutorService WORK_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "charcoal-worker-thread");
            t.setDaemon(true);
            return t;
          });

  private final CharcoalProfiler profiler =
      new CharcoalProfiler(Duration.ofMillis(500), SAMPLING_EXECUTOR, WORK_EXECUTOR);

  @Override
  public void afterOperationSetUp(String benchmark, int opIndex, boolean isLastOp) {
    profiler.clock.start();
  }

  @Override
  public void beforeOperationTearDown(String benchmark, int opIndex, long durationNanos) {
    profiler.clock.stop();
  }

  @Override
  public void beforeBenchmarkTearDown(String benchmark) {
    logger.info("TICKS:");
    logger.info(String.format("%s", profiler.clock.ticks()));
    List<List<TaskPower>> data =
        profiler.clock.ticks().stream()
            .map(ts -> profiler.taskPower.sample(ts))
            .map(
                power ->
                    power.values().stream().sorted(comparing(TaskPower::getCpu)).collect(toList()))
            .collect(toList());
    logger.info("START:");
    logger.info(String.format("%s", data.get(0)));
    logger.info("END:");
    logger.info(String.format("%s", data.get(data.size() - 1)));
  }
}
