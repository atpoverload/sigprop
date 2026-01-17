package yuca.profiler;

import charcoal.prop.ClockSignal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import yuca.profiler.linux.powercap.PowercapPowerSignal;
import yuca.profiler.linux.powercap.PowercapSignal;

public final class OfflinePowercapProfiler implements YucaProfiler {
  public final ClockSignal clock;
  public final PowercapSignal socketEnergy;

  public PowercapPowerSignal socketPower;

  private final Supplier<Duration> periodSource;
  private final ScheduledExecutorService workExecutor;

  private boolean isRunning = false;

  public OfflinePowercapProfiler(
      Supplier<Instant> timeSource,
      Supplier<Duration> periodSource,
      ScheduledExecutorService clockExecutor,
      ScheduledExecutorService workExecutor) {
    this.periodSource = periodSource;
    this.workExecutor = workExecutor;
    this.clock = new ClockSignal(timeSource, periodSource, clockExecutor, workExecutor);
    this.socketEnergy = this.clock.map(() -> new PowercapSignal(workExecutor));
  }

  @Override
  public void start() {
    synchronized (this) {
      if (!isRunning) {
        clock.start();
        socketPower = socketEnergy.asyncMap(me -> new PowercapPowerSignal(me, workExecutor));
        for (Instant tick : this.clock.ticks()) {
          socketEnergy.update(tick);
        }
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
