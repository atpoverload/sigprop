package yuca.profiler.linux.jiffies;

import charcoal.SourceSignal;
import charcoal.prop.AdjacentTimelineSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class TaskJiffiesRateSignal
    extends AdjacentTimelineSignal<Map<Long, TaskJiffies>, Map<Long, TaskJiffiesRate>> {
  public TaskJiffiesRateSignal(SourceSignal<Map<Long, TaskJiffies>> source, Executor executor) {
    super(source, executor);
  }

  @Override
  protected Map<Long, TaskJiffiesRate> compute(
      Instant start, Instant end, Map<Long, TaskJiffies> first, Map<Long, TaskJiffies> second) {
    if (start.equals(end)) {
      return Map.of();
    }
    return ProcTask.difference(start, end, first, second);
  }
}
