package charcoal.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import charcoal.profiler.CharcoalProfiler;
import charcoal.profiler.linux.freq.CpuFrequency;
import charcoal.profiler.linux.jiffies.TaskPower;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;

public class CharcoalDacapoCallback extends Callback {
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

  public CharcoalDacapoCallback(CommandLineArgs args) {
    super(args);
  }

  @Override
  public void start(String benchmark) {
    profiler.clock.start();
    super.start(benchmark);
  }

  @Override
  public void complete(String benchmark, boolean valid, boolean warmup) {
    super.complete(benchmark, valid, warmup);
    profiler.clock.stop();
  }

  @Override
  public boolean runAgain() {
    // if we have run every iteration, dump the data and terminate
    if (!super.runAgain()) {
      logger.info("TICKS:");
      logger.info(String.format("%s", profiler.clock.ticks()));
      List<List<TaskPower>> taskPower =
          profiler.clock.ticks().stream()
              .map(ts -> profiler.taskPower.sample(ts))
              .map(
                  power ->
                      power.values().stream()
                          .sorted(comparing(TaskPower::getCpu))
                          .collect(toList()))
              .collect(toList());
      logger.info("START:");
      logger.info(String.format("%s", taskPower.get(0)));
      logger.info("END:");
      logger.info(String.format("%s", taskPower.get(taskPower.size() - 1)));
      List<List<CpuFrequency>> freqs =
          profiler.clock.ticks().stream()
              .map(ts -> profiler.freqs.sample(ts))
              .map(
                  power ->
                      power.values().stream()
                          .sorted(comparing(CpuFrequency::getCpu))
                          .collect(toList()))
              .collect(toList());
      logger.info("START:");
      logger.info(String.format("%s", freqs.get(0)));
      logger.info("END:");
      logger.info(String.format("%s", freqs.get(freqs.size() - 1)));
      return false;
    } else {
      return true;
    }
  }
}
