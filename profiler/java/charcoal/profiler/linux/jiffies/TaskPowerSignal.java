package charcoal.profiler.linux.jiffies;

import charcoal.SourceSignal;
import charcoal.profiler.linux.powercap.PowercapPower;
import charcoal.profiler.units.Power;
import charcoal.prop.BiMappingSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public final class TaskPowerSignal
    extends BiMappingSignal<
        Map<Long, TaskActivityRate>, Map<Integer, PowercapPower>, Map<Long, TaskPower>>
    implements Power {
  public TaskPowerSignal(
      SourceSignal<Map<Long, TaskActivityRate>> tasks,
      SourceSignal<Map<Integer, PowercapPower>> cpus,
      Executor executor) {
    super(tasks, cpus, executor);
  }

  @Override
  protected Map<Long, TaskPower> compute(
      Instant timestamp, Map<Long, TaskActivityRate> first, Map<Integer, PowercapPower> second) {
    if (first.isEmpty() || second.isEmpty()) {
      return Map.of();
    }
    return ProcTask.taskPower(first, second);
  }
}
