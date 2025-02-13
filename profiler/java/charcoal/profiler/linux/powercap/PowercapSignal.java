package charcoal.profiler.linux.powercap;

import charcoal.profiler.units.Energy;
import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;

public final class PowercapSignal extends GeneratingSignal<Map<Integer, PowercapEnergy>>
    implements Energy {
  public PowercapSignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, PowercapEnergy> generate() {
    return Powercap.sample();
  }
}
