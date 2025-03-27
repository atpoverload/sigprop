package yuca.profiler.linux.powercap;

import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;

public final class PowercapSignal extends GeneratingSignal<Map<Integer, PowercapEnergy>> {
  public PowercapSignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, PowercapEnergy> compute() {
    return Powercap.samplePowercap();
  }
}
