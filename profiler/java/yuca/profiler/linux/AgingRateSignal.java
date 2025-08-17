package yuca.profiler.linux;

import static java.util.stream.Collectors.toMap;

import charcoal.SourceSignal;
import charcoal.prop.MappingSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class AgingRateSignal
    extends MappingSignal<Map<Integer, ThermalZoneTemperature>, Map<Integer, AgingRate>> {
  private static final double E_0 = 0.1897; // transistor channel energy in eV
  private static final double B = 0.075; // trap distribution parameter in eV nm/V
  private static final double k_b = 8.6173303e-5; // boltzmann's constant in eV/K
  private static final double V_dd = 0.070; // transistor supply voltage in V
  private static final double t_ox = 0.9; // effective oxide thickness in 0.9 nm
  private static final double epsilon = B * V_dd / t_ox - E_0; // Aging parameter

  private static final double computeAging(int temperature) {
    return Math.exp(epsilon * (temperature + 273.15) / k_b);
  }

  public AgingRateSignal(
      SourceSignal<Map<Integer, ThermalZoneTemperature>> thermalZones, Executor executor) {
    super(thermalZones, executor);
  }

  @Override
  protected Map<Integer, AgingRate> compute(
      Instant timestamp, Map<Integer, ThermalZoneTemperature> sockets) {
    System.out.println(sockets);
    return sockets.entrySet().stream()
        .collect(
            toMap(
                e -> e.getKey(),
                e ->
                    AgingRate.newBuilder()
                        // TODO: i don't know if this is accurate for multi-socket
                        .setSocket(e.getValue().getZoneId())
                        .setAging(computeAging(e.getValue().getTemperature()))
                        .build()));
  }
}
