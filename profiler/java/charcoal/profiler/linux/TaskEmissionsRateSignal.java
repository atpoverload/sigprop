package charcoal.profiler.linux;

import static java.util.stream.Collectors.toMap;

import charcoal.SourceSignal;
import charcoal.profiler.emissions.CarbonLocale;
import charcoal.prop.MappingSignal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;

public class TaskEmissionsRateSignal
    extends MappingSignal<Map<Long, TaskPower>, Map<Long, TaskEmissionsRate>> {
  public final CarbonLocale locale;
  public final double carbonIntensity;

  public TaskEmissionsRateSignal(
      CarbonLocale locale, SourceSignal<Map<Long, TaskPower>> tasks, Executor executor) {
    super(tasks, executor);
    this.locale = locale;
    // carbon intensity should be in gCO2 / KWH, so we convert to gCO2 / (watt * second) here
    this.carbonIntensity = locale.carbonIntensity / 60 / 60 / 1000;
  }

  @Override
  protected Map<Long, TaskEmissionsRate> compute(Instant timestamp, Map<Long, TaskPower> tasks) {
    return tasks.entrySet().stream()
        .collect(
            toMap(
                e -> e.getKey(),
                e ->
                    TaskEmissionsRate.newBuilder()
                        .setTask(e.getValue().getTask())
                        .setCpu(e.getValue().getCpu())
                        .setEmissions(e.getValue().getPower() * carbonIntensity)
                        .build()));
  }
}
