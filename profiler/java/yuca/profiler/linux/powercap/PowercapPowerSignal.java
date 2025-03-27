package yuca.profiler.linux.powercap;

import charcoal.SourceSignal;
import yuca.profiler.linux.SocketPower;
import charcoal.prop.AdjacentTimelineSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class PowercapPowerSignal
    extends AdjacentTimelineSignal<Map<Integer, PowercapEnergy>, Map<Integer, SocketPower>> {
  public PowercapPowerSignal(SourceSignal<Map<Integer, PowercapEnergy>> source, Executor executor) {
    super(source, executor);
  }

  @Override
  protected Map<Integer, SocketPower> compute(
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
