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
  private final int normalFrequency;

  public AmortizedEmissionsRateSignal(
      double embodiedCarbon,
      int normalFrequency,
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
    System.out.println(cpuFreqs);
    HashMap<Integer, Double> emissions = new HashMap<>();
    for (CpuFrequency freq : cpuFreqs.values()) {
      System.out.println("????");
      System.out.println(freq);
      int socket = SOCKETS_MAP[freq.getCpu()];
      System.out.println(embodiedCarbon);
      System.out.println(freq.getFrequency());
      System.out.println(aging.get(socket).getAging());
      System.out.println(normalFrequency);
      System.out.println("!!!!");
      double emission = embodiedCarbon * freq.getFrequency() * aging.get(socket).getAging() / normalFrequency;
      System.out.println(emission);
      emissions.putIfAbsent(socket, 0.0);
      emissions.put(
          socket, emissions.get(socket) + emission);
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
    System.out.println(emissions);
    System.out.println(amortized);
    return amortized;
  }
}
