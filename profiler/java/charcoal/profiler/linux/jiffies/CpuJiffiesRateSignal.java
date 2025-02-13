package charcoal.profiler.linux.jiffies;

import charcoal.SourceSignal;
import charcoal.prop.AdjacentTimelineSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class CpuJiffiesRateSignal
    extends AdjacentTimelineSignal<Map<Integer, CpuJiffies>, Map<Integer, CpuJiffiesRate>>
    implements JiffiesRate {
  public CpuJiffiesRateSignal(SourceSignal<Map<Integer, CpuJiffies>> source, Executor executor) {
    super(source, executor);
  }

  @Override
  protected Map<Integer, CpuJiffiesRate> compute(
      Instant start, Instant end, Map<Integer, CpuJiffies> first, Map<Integer, CpuJiffies> second) {
    if (start.equals(end)) {
      return Map.of();
    }
    return ProcStat.difference(start, end, first, second);
  }
}
