package yuca.profiler.linux;

import static java.util.stream.Collectors.toMap;

import charcoal.SourceSignal;
import charcoal.prop.MappingSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class AgingRateSignal
    extends MappingSignal<Map<Integer, ThermalZoneTemperature>, Map<Integer, AgingRate>> {
  public AgingRateSignal(
      SourceSignal<Map<Integer, ThermalZoneTemperature>> thermalZones, Executor executor) {
    super(thermalZones, executor);
  }

  @Override
  protected Map<Integer, AgingRate> compute(
      Instant timestamp, Map<Integer, ThermalZoneTemperature> sockets) {
    return sockets.entrySet().stream()
        .collect(
            toMap(
                e -> e.getKey(),
                e ->
                    AgingRate.newBuilder()
                        // TODO: i don't know if this is accurate for multi-socket
                        .setSocket(e.getValue().getZoneId())
                        .setAging(Math.exp(1 / e.getValue().getTemperature()))
                        .build()));
  }
}
