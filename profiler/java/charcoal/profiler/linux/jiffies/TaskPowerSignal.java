// package charcoal.profiler.linux.jiffies;

// import charcoal.SourceSignal;
// import charcoal.profiler.linux.SocketPower;
// import charcoal.profiler.linux.TaskPower;
// import charcoal.prop.BiMappingSignal;
// import java.time.Instant;
// import java.util.Map;
// import java.util.concurrent.Executor;

// public final class TaskPowerSignal
//     extends BiMappingSignal<
//         Map<Long, TaskActivityRate>, Map<Integer, SocketPower>, Map<Long, TaskPower>> {
//   public TaskPowerSignal(
//       SourceSignal<Map<Long, TaskActivityRate>> tasks,
//       SourceSignal<Map<Integer, SocketPower>> cpus,
//       Executor executor) {
//     super(tasks, cpus, executor);
//   }

//   @Override
//   protected Map<Long, TaskPower> compute(
//       Instant timestamp, Map<Long, TaskActivityRate> first, Map<Integer, SocketPower> second) {
//     if (first.isEmpty() || second.isEmpty()) {
//       return Map.of();
//     }
//     return ProcTask.taskPower(first, second);
//   }
// }
