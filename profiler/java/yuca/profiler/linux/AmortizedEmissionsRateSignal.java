package yuca.profiler.linux;

import charcoal.SourceSignal;
import charcoal.prop.BiMappingSignal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Signal that computes the amortized emissions of a system's CPUs. Consult
 * https://www.sciencedirect.com/science/article/abs/pii/S0026271413004368 for more information.
 */
public final class AmortizedEmissionsRateSignal
    extends BiMappingSignal<
        Map<Integer, CpuFrequency>, Map<Integer, AgingRate>, Map<Integer, AmortizedEmissionsRate>> {
  private static final int[] SOCKETS_MAP = CpuInfo.getCpuSocketMapping();

  private final double embodiedCarbon;
  private final long normalFrequency;

  public AmortizedEmissionsRateSignal(
      double embodiedCarbon,
      long normalFrequency,
      SourceSignal<Map<Integer, CpuFrequency>> cpuFreqs,
      SourceSignal<Map<Integer, AgingRate>> aging,
      Executor executor) {
    super(cpuFreqs, aging, executor);
    this.embodiedCarbon = embodiedCarbon;
    this.normalFrequency = normalFrequency;
  }

  @Override
  protected Map<Integer, AmortizedEmissionsRate> compute(
      Instant timestamp, Map<Integer, CpuFrequency> cpuFreqs, Map<Integer, AgingRate> aging) {
    HashMap<Integer, Double> emissions = new HashMap<>();
    for (CpuFrequency freq : cpuFreqs.values()) {
      int socket = SOCKETS_MAP[freq.getCpu()];
      if (!aging.containsKey(socket)) {
        continue;
      }
      double emission =
          embodiedCarbon * freq.getFrequency() * aging.get(socket).getAging() / normalFrequency;
      emissions.putIfAbsent(socket, 0.0);
      emissions.put(socket, emissions.get(socket) + emission);
    }
    HashMap<Integer, AmortizedEmissionsRate> amortized = new HashMap<>();
    for (int socket : emissions.keySet()) {
      amortized.put(
          socket,
          AmortizedEmissionsRate.newBuilder()
              .setSocket(socket)
              .setEmissions(emissions.get(socket))
              .build());
    }
    return amortized;
  }
}
