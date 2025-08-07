package yuca.profiler;

import charcoal.prop.ButtonSignal;
import charcoal.util.Timestamps;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import yuca.profiler.emissions.CarbonLocale;
import yuca.profiler.linux.SocketEmissionsRateSignal;
import yuca.profiler.linux.powercap.PowercapPowerSignal;
import yuca.profiler.linux.powercap.PowercapSignal;

public final class E2EOperationalCarbonProfiler implements Profiler {
  private static final CarbonLocale DEFAULT_LOCALE = getDefaultLocale();

  private static final CarbonLocale getDefaultLocale() {
    try {
      return CarbonLocale.valueOf(System.getProperty("yuca.profiler.emissions.locale", "USA"));
    } catch (Exception e) {
      return CarbonLocale.GLOBAL;
    }
  }

  public final ButtonSignal button;
  public final PowercapPowerSignal socketPower;
  public final SocketEmissionsRateSignal socketEmissions;

  private boolean isRunning = false;
  private Instant start = Instant.EPOCH;
  private Instant end = Instant.EPOCH;

  public E2EOperationalCarbonProfiler(ScheduledExecutorService workExecutor) {
    button = new ButtonSignal(Timestamps::now, workExecutor);
    socketPower =
        button
            .map(() -> new PowercapSignal(workExecutor))
            .map(me -> new PowercapPowerSignal(me, workExecutor));
    socketEmissions =
        socketPower.map(me -> new SocketEmissionsRateSignal(DEFAULT_LOCALE, me, workExecutor));
  }

  @Override
  public void start() {
    synchronized (this) {
      if (!isRunning) {
        start = button.sample(null);
        button.propagate(start);
        isRunning = true;
      }
    }
  }

  @Override
  public void stop() {
    synchronized (this) {
      if (isRunning) {
        end = button.sample(null);
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
