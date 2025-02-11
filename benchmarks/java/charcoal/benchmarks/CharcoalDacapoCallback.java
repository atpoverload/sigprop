package charcoal.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import charcoal.profiler.linux.jiffies.CpuJiffiesRateSignal;
import charcoal.profiler.linux.jiffies.CpuJiffiesSignal;
import charcoal.profiler.linux.jiffies.TaskActivityRateSignal;
import charcoal.profiler.linux.jiffies.TaskJiffiesRateSignal;
import charcoal.profiler.linux.jiffies.TaskJiffiesSignal;
import charcoal.profiler.linux.jiffies.TaskPower;
import charcoal.profiler.linux.jiffies.TaskPowerSignal;
import charcoal.profiler.linux.powercap.PowercapPowerSignal;
import charcoal.profiler.linux.powercap.PowercapSignal;
import charcoal.prop.ClockSignal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;

public class CharcoalDacapoCallback extends Callback {
  private static final Logger logger = getLogger();

  private static final ScheduledExecutorService EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "charcoal-sampling-thread");
            t.setDaemon(true);
            return t;
          });

  private final ClockSignal clock = ClockSignal.fixedPeriod(Duration.ofMillis(500), EXECUTOR);
  private final TaskPowerSignal power =
      clock
          .map(() -> TaskJiffiesSignal.current(EXECUTOR))
          .map(me -> new TaskJiffiesRateSignal(me, EXECUTOR))
          .compose(
              clock
                  .map(() -> new CpuJiffiesSignal(EXECUTOR))
                  .map(me -> new CpuJiffiesRateSignal(me, EXECUTOR)))
          .map((me, them) -> new TaskActivityRateSignal(me, them, EXECUTOR))
          .compose(
              clock
                  .map(me -> new PowercapSignal(EXECUTOR))
                  .map(me -> new PowercapPowerSignal(me, EXECUTOR)))
          .map((me, them) -> new TaskPowerSignal(me, them, EXECUTOR));

  public CharcoalDacapoCallback(CommandLineArgs args) {
    super(args);
    // activity.map(LoggerSink::forSigprop);
  }

  @Override
  public void start(String benchmark) {
    clock.start();
    super.start(benchmark);
  }

  @Override
  public void complete(String benchmark, boolean valid, boolean warmup) {
    super.complete(benchmark, valid, warmup);
    clock.stop();
  }

  @Override
  public boolean runAgain() {
    // if we have run every iteration, dump the data and terminate
    if (!super.runAgain()) {
      logger.info("TICKS:");
      logger.info(String.format("%s", clock.ticks()));
      List<List<TaskPower>> data =
          clock.ticks().stream()
              .map(power::sample)
              .map(t -> t.values().stream().sorted(comparing(TaskPower::getCpu)).collect(toList()))
              .collect(toList());
      logger.info("START:");
      logger.info(String.format("%s", data.get(0)));
      logger.info("END:");
      logger.info(String.format("%s", data.get(data.size() - 1)));
      return false;
    } else {
      return true;
    }
  }
}
