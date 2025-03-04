package charcoal.profiler;

import charcoal.profiler.emissions.CarbonLocale;
import charcoal.profiler.linux.SocketEmissionsRateSignal;
import charcoal.profiler.linux.TaskEmissionsRateSignal;
import charcoal.profiler.linux.TaskPowerSignal;
import charcoal.profiler.linux.freq.CpuFrequencySignal;
import charcoal.profiler.linux.jiffies.CpuJiffiesRateSignal;
import charcoal.profiler.linux.jiffies.CpuJiffiesSignal;
import charcoal.profiler.linux.jiffies.TaskActivityRateSignal;
import charcoal.profiler.linux.jiffies.TaskJiffiesRateSignal;
import charcoal.profiler.linux.jiffies.TaskJiffiesSignal;
import charcoal.profiler.linux.powercap.PowercapPowerSignal;
import charcoal.profiler.linux.powercap.PowercapSignal;
import charcoal.prop.ClockSignal;
import charcoal.util.Timestamps;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

public final class CharcoalProfiler {
  private static final CarbonLocale DEFAULT_LOCALE = getDefaultLocale();

  private static final CarbonLocale getDefaultLocale() {
    try {
      return CarbonLocale.valueOf(System.getProperty("charcoal.profiler.emissions.locale", "USA"));
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

  public CharcoalProfiler(
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
