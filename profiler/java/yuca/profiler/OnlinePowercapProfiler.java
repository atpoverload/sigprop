package yuca.profiler;

import charcoal.prop.ClockSignal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import yuca.profiler.linux.powercap.PowercapPowerSignal;
import yuca.profiler.linux.powercap.PowercapSignal;

public final class OnlinePowercapProfiler implements YucaProfiler {
  public final ClockSignal clock;
  public final PowercapPowerSignal socketPower;

  private final Supplier<Duration> periodSource;

  private boolean isRunning = false;

  public OnlinePowercapProfiler(
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
    }
    return profile.build();
  }
}
