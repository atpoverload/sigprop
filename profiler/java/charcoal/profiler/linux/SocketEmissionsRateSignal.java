package charcoal.profiler.linux;

import static java.util.stream.Collectors.toMap;

import charcoal.SourceSignal;
import charcoal.profiler.emissions.CarbonLocale;
import charcoal.prop.MappingSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public class SocketEmissionsRateSignal
    extends MappingSignal<Map<Integer, SocketPower>, Map<Integer, SocketEmissionsRate>> {
  public final CarbonLocale locale;
  public final double carbonIntensity;

  public SocketEmissionsRateSignal(
      CarbonLocale locale, SourceSignal<Map<Integer, SocketPower>> tasks, Executor executor) {
    super(tasks, executor);
    this.locale = locale;
    // carbon intensity should be in gCO2 / KWH, so we convert to gCO2 / (watt * second) here
    this.carbonIntensity = locale.carbonIntensity / 60 / 60 / 1000;
  }

  @Override
  protected Map<Integer, SocketEmissionsRate> compute(
      Instant timestamp, Map<Integer, SocketPower> sockets) {
    return sockets.entrySet().stream()
        .collect(
            toMap(
                e -> e.getKey(),
                e ->
                    SocketEmissionsRate.newBuilder()
                        .setSocket(e.getValue().getSocket())
                        .setEmissions(e.getValue().getPower() * carbonIntensity)
                        .build()));
  }
}
