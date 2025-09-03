package yuca.profiler;

import charcoal.prop.ButtonSignal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import yuca.profiler.emissions.CarbonLocale;
import yuca.profiler.linux.SocketEmissionsRateSignal;
import yuca.profiler.linux.powercap.PowercapPowerSignal;
import yuca.profiler.linux.powercap.PowercapSignal;

public final class EndToEndProfiler implements YucaProfiler {
  public final PowercapPowerSignal socketPower;
  public final SocketEmissionsRateSignal socketEmissions;

  private final Supplier<Instant> timeSource;
  private final ButtonSignal button;

  private boolean isRunning = false;
  private Instant start = Instant.EPOCH;
  private Instant end = Instant.EPOCH;

  public EndToEndProfiler(
      Supplier<Instant> timeSource, CarbonLocale locale, ScheduledExecutorService workExecutor) {
    this.timeSource = timeSource;
    this.button = new ButtonSignal(workExecutor);
    this.socketPower =
        this.button
            .map(() -> new PowercapSignal(workExecutor))
            .map(me -> new PowercapPowerSignal(me, workExecutor));
    this.socketEmissions =
        this.socketPower.map(me -> new SocketEmissionsRateSignal(locale, me, workExecutor));
  }

  @Override
  public void start() {
    synchronized (this) {
      if (!isRunning) {
        start = timeSource.get();
        this.button.propagate(start);
        isRunning = true;
      }
    }
  }

  @Override
  public void stop() {
    synchronized (this) {
      if (isRunning) {
        end = timeSource.get();
        button.propagate(end);
        isRunning = false;
      }
    }
  }

  @Override
  public YucaProfile getProfile() {
    YucaProfile.Builder profile = YucaProfile.newBuilder();
    for (Instant ts : List.of(start, end)) {
      Timestamp timestamp =
          Timestamp.newBuilder().setSecs(ts.getEpochSecond()).setNanos(ts.getNano()).build();
      profile.addSocketPower(
          YucaProfile.SocketsPowers.newBuilder()
              .setTimestamp(timestamp)
              .addAllPower(this.socketPower.sample(end).values()));
      profile.addSocketEmissions(
          YucaProfile.SocketsEmissionsRates.newBuilder()
              .setTimestamp(timestamp)
              .addAllEmissions(this.socketEmissions.sample(end).values()));
    }
    return profile.build();
  }
}
