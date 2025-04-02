package yuca.profiler.linux;

import charcoal.SourceSignal;
import charcoal.prop.BiMappingSignal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public final class TaskPowerSignal
    extends BiMappingSignal<
        Map<Long, TaskActivityRate>, Map<Integer, SocketPower>, Map<Long, TaskPower>> {
  private static final int[] SOCKETS_MAP = CpuInfo.getCpuSocketMapping();

  public TaskPowerSignal(
      SourceSignal<Map<Long, TaskActivityRate>> tasks,
      SourceSignal<Map<Integer, SocketPower>> cpus,
      Executor executor) {
    super(tasks, cpus, executor);
  }

  @Override
  protected Map<Long, TaskPower> compute(
      Instant timestamp, Map<Long, TaskActivityRate> tasks, Map<Integer, SocketPower> sockets) {
    if (tasks.isEmpty() || sockets.isEmpty()) {
      return Map.of();
    }
    HashMap<Long, TaskPower> power = new HashMap<>();
    for (TaskActivityRate task : tasks.values()) {
      power.put(
          task.getTask().getTaskId(),
          TaskPower.newBuilder()
              .setTask(task.getTask())
              .setCpu(task.getCpu())
              .setPower(task.getActivity() * sockets.get(SOCKETS_MAP[task.getCpu()]).getPower())
              .build());
    }
    return power;
  }
}
