// package charcoal.profiler.linux.jiffies;

// import static java.util.stream.Collectors.toMap;

// import charcoal.prop.MappingSignal;
// import java.time.Instant;
// import java.util.Map;
// import java.util.concurrent.Executor;

// public class TaskEmissionsRateSignal
//     extends MappingSignal<Map<Long, TaskPower>, Map<Long, TaskEmissionsRate>> {
//   private final double carbonIntensity;

//   public TaskEmissionsRateSignal(
//       double carbonIntensity, Map<Long, TaskPower> tasks, Executor executor) {
//     super(tasks, executor);
//     // carbon intensity should be in gCO2 / KWH, so we convert to gCO2 / (watt * second) here
//     this.carbonIntensity = carbonIntensity / 60 / 60 / 1000;
//   }

//   @Override
//   protected Map<Long, TaskEmissionsRate> compute(Instant timestamp, Map<Long, TaskPower> tasks) {
//     return tasks.entrySet().stream()
//         .collect(toMap(e -> e.getKey(), e -> e.getValue().getPower() * carbonIntensity));
//   }
// }
