package charcoal.profiler.linux.jiffies;

import charcoal.SourceSignal;
import charcoal.profiler.units.ActivityRate;
import charcoal.prop.BiMappingSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class TaskActivityRateSignal
    extends BiMappingSignal<
        Map<Long, TaskJiffiesRate>, Map<Integer, CpuJiffiesRate>, Map<Long, TaskActivityRate>>
    implements ActivityRate {
  public TaskActivityRateSignal(
      SourceSignal<Map<Long, TaskJiffiesRate>> tasks,
      SourceSignal<Map<Integer, CpuJiffiesRate>> cpus,
      Executor executor) {
    super(tasks, cpus, executor);
  }

  @Override
  protected Map<Long, TaskActivityRate> compute(
      Instant timestamp, Map<Long, TaskJiffiesRate> first, Map<Integer, CpuJiffiesRate> second) {
    return ProcTask.taskActivityRate(first, second);
  }
}
