package yuca.profiler.linux;

import charcoal.SourceSignal;
import charcoal.prop.BiMappingSignal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public final class AmortizedEmissionsRateSignal
    extends BiMappingSignal<
        Map<Integer, CpuFrequency>, Map<Integer, AgingRate>, Map<Integer, AmortizedEmissionsRate>> {
  private static final int[] SOCKETS_MAP = CpuInfo.getCpuSocketMapping();

  private final double embodiedCarbon;

  public AmortizedEmissionsRateSignal(
      double embodiedCarbon,
      SourceSignal<Map<Integer, CpuFrequency>> cpuFreqs,
      SourceSignal<Map<Integer, AgingRate>> aging,
      Executor executor) {
    super(cpuFreqs, aging, executor);
    this.embodiedCarbon = embodiedCarbon;
  }

  @Override
  protected Map<Integer, AmortizedEmissionsRate> compute(
      Instant timestamp, Map<Integer, CpuFrequency> cpuFreqs, Map<Integer, AgingRate> aging) {
    HashMap<Integer, Double> emissions = new HashMap<>();
    for (CpuFrequency freq : cpuFreqs.values()) {
      int socket = SOCKETS_MAP[freq.getCpu()];
      emissions.putIfAbsent(socket, 0.0);
      emissions.put(
          socket,
          emissions.get(socket) + freq.getObservedFrequency() * aging.get(socket).getAging());
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
