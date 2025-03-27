package yuca.profiler;

import yuca.profiler.emissions.CarbonLocale;
import yuca.profiler.linux.SocketEmissionsRateSignal;
import yuca.profiler.linux.TaskEmissionsRateSignal;
import yuca.profiler.linux.TaskPowerSignal;
import yuca.profiler.linux.freq.CpuFrequencySignal;
import yuca.profiler.linux.jiffies.CpuJiffiesRateSignal;
import yuca.profiler.linux.jiffies.CpuJiffiesSignal;
import yuca.profiler.linux.jiffies.TaskActivityRateSignal;
import yuca.profiler.linux.jiffies.TaskJiffiesRateSignal;
import yuca.profiler.linux.jiffies.TaskJiffiesSignal;
import yuca.profiler.linux.powercap.PowercapPowerSignal;
import yuca.profiler.linux.powercap.PowercapSignal;
import charcoal.prop.ClockSignal;
import charcoal.util.Timestamps;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

public final class YucaProfiler {
  private static final CarbonLocale DEFAULT_LOCALE = getDefaultLocale();

  private static final CarbonLocale getDefaultLocale() {
    try {
      return CarbonLocale.valueOf(System.getProperty("yuca.profiler.emissions.locale", "USA"));
    } catch (Exception e) {
      return CarbonLocale.GLOBAL;
    }
  }

  public final ClockSignal clock;
  public final TaskActivityRateSignal activity;
  public final PowercapPowerSignal socketPower;
  public final TaskPowerSignal taskPower;
  public final SocketEmissionsRateSignal socketEmissions;
  public final TaskEmissionsRateSignal taskEmissions;
  public final CpuFrequencySignal freqs;

  public YucaProfiler(
      Duration period,
      ScheduledExecutorService clockExecutor,
      ScheduledExecutorService workExecutor) {
    clock = new ClockSignal(Timestamps::now, () -> period, clockExecutor, workExecutor);
    activity =
        clock
            .map(() -> TaskJiffiesSignal.current(workExecutor))
            .asyncMap(me -> new TaskJiffiesRateSignal(me, workExecutor))
            .compose(
                clock
                    .map(() -> new CpuJiffiesSignal(workExecutor))
                    .asyncMap(me -> new CpuJiffiesRateSignal(me, workExecutor)))
            .map((me, them) -> new TaskActivityRateSignal(me, them, workExecutor));
    socketPower =
        clock
            .map(() -> new PowercapSignal(workExecutor))
            .asyncMap(me -> new PowercapPowerSignal(me, workExecutor));
    socketEmissions =
        socketPower.map(me -> new SocketEmissionsRateSignal(DEFAULT_LOCALE, me, workExecutor));
    taskPower =
        activity
            .compose(socketPower)
            .map((me, them) -> new TaskPowerSignal(me, them, workExecutor));
    taskEmissions =
        taskPower.map(me -> new TaskEmissionsRateSignal(DEFAULT_LOCALE, me, workExecutor));
    freqs = clock.map(() -> new CpuFrequencySignal(workExecutor));
  }
}
