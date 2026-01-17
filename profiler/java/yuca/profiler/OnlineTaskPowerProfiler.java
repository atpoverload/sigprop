package yuca.profiler;

import charcoal.prop.ClockSignal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import yuca.profiler.linux.TaskPowerSignal;
import yuca.profiler.linux.jiffies.CpuJiffiesRateSignal;
import yuca.profiler.linux.jiffies.CpuJiffiesSignal;
import yuca.profiler.linux.jiffies.TaskActivityRateSignal;
import yuca.profiler.linux.jiffies.TaskJiffiesRateSignal;
import yuca.profiler.linux.jiffies.TaskJiffiesSignal;
import yuca.profiler.linux.powercap.PowercapPowerSignal;
import yuca.profiler.linux.powercap.PowercapSignal;

public final class OnlineTaskPowerProfiler implements YucaProfiler {
  public final ClockSignal clock;
  public final TaskActivityRateSignal activity;
  public final PowercapPowerSignal socketPower;
  public final TaskPowerSignal taskPower;

  private final Supplier<Duration> periodSource;

  private boolean isRunning = false;

  public OnlineTaskPowerProfiler(
      Supplier<Instant> timeSource,
      Supplier<Duration> periodSource,
      ScheduledExecutorService clockExecutor,
      ScheduledExecutorService workExecutor) {
    this.periodSource = periodSource;
    this.clock = new ClockSignal(timeSource, periodSource, clockExecutor, workExecutor);
    this.socketPower =
        this.clock
            .map(() -> new PowercapSignal(workExecutor))
            .asyncMap(me -> new PowercapPowerSignal(me, workExecutor));
    this.activity =
        this.clock
            .map(() -> TaskJiffiesSignal.current(workExecutor))
            .asyncMap(me -> new TaskJiffiesRateSignal(me, workExecutor))
            .compose(
                this.clock
                    .map(() -> new CpuJiffiesSignal(workExecutor))
                    .asyncMap(me -> new CpuJiffiesRateSignal(me, workExecutor)))
            .map((me, them) -> new TaskActivityRateSignal(me, them, workExecutor));
    this.taskPower =
        activity
            .compose(this.socketPower)
            .map((me, them) -> new TaskPowerSignal(me, them, workExecutor));
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
      profile.addSocketPower(
          YucaProfile.SocketsPowers.newBuilder()
              .setTimestamp(timestamp)
              .addAllPower(this.socketPower.sample(tick).values()));
      profile.addTaskActivity(
          YucaProfile.TasksActivities.newBuilder()
              .setTimestamp(timestamp)
              .addAllActivity(this.activity.sample(tick).values()));
      profile.addTaskPower(
          YucaProfile.TasksPowers.newBuilder()
              .setTimestamp(timestamp)
              .addAllPower(this.taskPower.sample(tick).values()));
    }
    return profile.build();
  }
}
