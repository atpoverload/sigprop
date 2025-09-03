package yuca.profiler;

import charcoal.prop.ClockSignal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
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

public final class OnlineYucaProfiler implements YucaProfiler {
  public final ClockSignal clock;
  public final TaskActivityRateSignal activity;
  public final PowercapPowerSignal socketPower;
  public final TaskPowerSignal taskPower;
  public final SocketEmissionsRateSignal socketEmissions;
  public final TaskEmissionsRateSignal taskEmissions;
  public final CpuFrequencySignal freqs;
  public final ThermalZonesSignal temperature;
  public final AmortizedEmissionsRateSignal amortizedEmissions;

  private final Supplier<Duration> periodSource;

  private boolean isRunning = false;

  public OnlineYucaProfiler(
      Supplier<Instant> timeSource,
      Supplier<Duration> periodSource,
      CarbonLocale locale,
      double systemEmbodiedCarbon,
      long normalFrequency,
      ScheduledExecutorService clockExecutor,
      ScheduledExecutorService workExecutor) {
    this.periodSource = periodSource;
    this.clock = new ClockSignal(timeSource, periodSource, clockExecutor, workExecutor);
    this.activity =
        this.clock
            .map(() -> TaskJiffiesSignal.current(workExecutor))
            .asyncMap(me -> new TaskJiffiesRateSignal(me, workExecutor))
            .compose(
                this.clock
                    .map(() -> new CpuJiffiesSignal(workExecutor))
                    .asyncMap(me -> new CpuJiffiesRateSignal(me, workExecutor)))
            .map((me, them) -> new TaskActivityRateSignal(me, them, workExecutor));
    this.socketPower =
        this.clock
            .map(() -> new PowercapSignal(workExecutor))
            .asyncMap(me -> new PowercapPowerSignal(me, workExecutor));
    this.socketEmissions =
        socketPower.map(me -> new SocketEmissionsRateSignal(locale, me, workExecutor));
    this.taskPower =
        activity
            .compose(this.socketPower)
            .map((me, them) -> new TaskPowerSignal(me, them, workExecutor));
    this.taskEmissions = taskPower.map(me -> new TaskEmissionsRateSignal(locale, me, workExecutor));
    this.freqs = clock.map(() -> new CpuFrequencySignal(workExecutor));
    this.temperature = clock.map(() -> new ThermalZonesSignal(workExecutor));
    this.amortizedEmissions =
        freqs
            .compose(temperature.map(me -> new AgingRateSignal(me, workExecutor)))
            .map(
                (me, other) ->
                    new AmortizedEmissionsRateSignal(
                        systemEmbodiedCarbon, normalFrequency, me, other, workExecutor));
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
    profile
        .getSessionBuilder()
        .getPeriodBuilder()
        .setSecs(periodSource.get().toSecondsPart())
        .setNanos(periodSource.get().toNanosPart());
    for (Instant tick : this.clock.ticks()) {
      Timestamp timestamp =
          Timestamp.newBuilder().setSecs(tick.getEpochSecond()).setNanos(tick.getNano()).build();
      profile.addCpuFreq(
          YucaProfile.CpusFrequencies.newBuilder()
              .setTimestamp(timestamp)
              .addAllFrequency(this.freqs.sample(tick).values()));
      profile.addTemperature(
          YucaProfile.ThermalZonesTemperatures.newBuilder()
              .setTimestamp(timestamp)
              .addAllTemperature(this.temperature.sample(tick).values()));
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
      profile.addAmortizedEmissions(
          YucaProfile.AmortizedEmissionsRates.newBuilder()
              .setTimestamp(timestamp)
              .addAllEmissions(this.amortizedEmissions.sample(tick).values()));
    }
    return profile.build();
  }
}
