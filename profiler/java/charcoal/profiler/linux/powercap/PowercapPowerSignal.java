package charcoal.profiler.linux.powercap;

import charcoal.SourceSignal;
import charcoal.profiler.units.Power;
import charcoal.prop.AdjacentTimelineSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class PowercapPowerSignal
    extends AdjacentTimelineSignal<Map<Integer, PowercapEnergy>, Map<Integer, PowercapPower>>
    implements Power {
  public PowercapPowerSignal(SourceSignal<Map<Integer, PowercapEnergy>> source, Executor executor) {
    super(source, executor);
  }

  @Override
  public Map<Integer, PowercapPower> compute(
      Instant start,
      Instant end,
      Map<Integer, PowercapEnergy> first,
      Map<Integer, PowercapEnergy> second) {
    if (start.equals(end)) {
      return Map.of();
    }
    return Powercap.difference(start, end, first, second);
  }
}
