package yuca.profiler.linux.thermal;

import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;
import yuca.profiler.linux.ThermalZoneTemperature;

public final class ThermalZonesSignal
    extends GeneratingSignal<Map<Integer, ThermalZoneTemperature>> {
  public ThermalZonesSignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, ThermalZoneTemperature> compute() {
    return SysThermal.sampleThermalZones();
  }
}
