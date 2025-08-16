package yuca.profiler;

import charcoal.prop.ClockSignal;
import charcoal.util.Timestamps;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import yuca.profiler.emissions.CarbonLocale;
import yuca.profiler.linux.AgingRateSignal;
import yuca.profiler.linux.AmortizedEmissionsRateSignal;
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
import yuca.profiler.linux.thermal.ThermalZonesSignal;

public final class YucaProfiler implements Profiler {
  private static final CarbonLocale DEFAULT_LOCALE = getDefaultLocale();
  private static final double EMBODIED_CARBON = getCpuEmbodiedCarbon();

  private static final CarbonLocale getDefaultLocale() {
    try {
      return CarbonLocale.valueOf(System.getProperty("yuca.profiler.emissions.locale", "USA"));
    } catch (Exception e) {
      return CarbonLocale.GLOBAL;
    }
  }

  private static final double getCpuEmbodiedCarbon() {
    try {
      return Double.parseDouble(System.getProperty("yuca.profiler.emissions.embodied", "1.0"));
    } catch (Exception e) {
      return 1.0;
    }
  }

  public final ClockSignal clock;
  public final TaskActivityRateSignal activity;
  public final PowercapPowerSignal socketPower;
  public final TaskPowerSignal taskPower;
  public final SocketEmissionsRateSignal socketEmissions;
  public final TaskEmissionsRateSignal taskEmissions;
  public final CpuFrequencySignal freqs;
  public final ThermalZonesSignal temperature;

  private boolean isRunning = false;

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
    temperature = clock.map(() -> new ThermalZonesSignal(workExecutor));
    // TODO: add the amortized carbon
    freqs
        .compose(temperature.map(me -> new AgingRateSignal(me, workExecutor)))
        .map(
            (me, other) ->
                new AmortizedEmissionsRateSignal(EMBODIED_CARBON, me, other, workExecutor));
  }

  @Override
  public void start() {
    synchronized (this) {
      if (!isRunning) {
        clock.start();
        isRunning = true;
      }
    }
  }

  @Override
  public void stop() {
    synchronized (this) {
      if (isRunning) {
        clock.stop();
        isRunning = false;
      }
    }
  }

  @Override
  public YucaProfile getProfile() {
    YucaProfile.Builder profile = YucaProfile.newBuilder();
    for (Instant tick : this.clock.ticks()) {
      Timestamp timestamp =
          Timestamp.newBuilder().setSecs(tick.getEpochSecond()).setNanos(tick.getNano()).build();
      profile.addCpuFreq(
          YucaProfile.CpusFrequencies.newBuilder()
              .setTimestamp(timestamp)
              .addAllFrequency(this.freqs.sample(tick).values()));
      profile.addSocketPower(
          YucaProfile.SocketsPowers.newBuilder()
              .setTimestamp(timestamp)
              .addAllPower(this.socketPower.sample(tick).values()));
      profile.addSocketEmissions(
          YucaProfile.SocketsEmissionsRates.newBuilder()
              .setTimestamp(timestamp)
              .addAllEmissions(this.socketEmissions.sample(tick).values()));
      profile.addTaskActivity(
          YucaProfile.TasksActivities.newBuilder()
              .setTimestamp(timestamp)
              .addAllActivity(this.activity.sample(tick).values()));
      profile.addTaskPower(
          YucaProfile.TasksPowers.newBuilder()
              .setTimestamp(timestamp)
              .addAllPower(this.taskPower.sample(tick).values()));
      profile.addTaskEmissions(
          YucaProfile.TasksEmissionsRates.newBuilder()
              .setTimestamp(timestamp)
              .addAllEmissions(this.taskEmissions.sample(tick).values()));
    }
    return profile.build();
  }
}
