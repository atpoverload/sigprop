package yuca.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;

import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public final class ProfilerUtil {
    private static final String OUTPUT =
      System.getProperty(
          "yuca.benchmarks.output",
          String.format("/tmp/dacapo-%d-report.pb", ProcessHandle.current().pid()));

  private static final ScheduledExecutorService SAMPLING_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "yuca-sampling-thread");
            t.setDaemon(true);
            return t;
          });

  private static final ScheduledExecutorService WORK_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "yuca-worker-thread");
            t.setDaemon(true);
            return t;
          });

  public static YucaProfiler newProfiler() {
    return new YucaProfiler(Duration.ofMillis(10), SAMPLING_EXECUTOR, WORK_EXECUTOR);
  }

  public static void dumpProfile(YucaProfiler profiler) {
    YucaProfile.Builder profile = YucaProfile.newBuilder();
      for (Instant tick : profiler.clock.ticks()) {
        YucaProfile.Timestamp timestamp =
            YucaProfile.Timestamp.newBuilder()
                .setSecs(tick.getEpochSecond())
                .setNanos(tick.getNano())
                .build();
        profile.addCpuFreq(
            YucaProfile.CpusFrequencies.newBuilder()
                .setTimestamp(timestamp)
                .addAllFrequency(profiler.freqs.sample(tick).values()));
        profile.addSocketPower(
            YucaProfile.SocketsPowers.newBuilder()
                .setTimestamp(timestamp)
                .addAllPower(profiler.socketPower.sample(tick).values()));
        profile.addSocketEmissions(
            YucaProfile.SocketsEmissionsRates.newBuilder()
                .setTimestamp(timestamp)
                .addAllEmissions(profiler.socketEmissions.sample(tick).values()));
        profile.addTaskActivity(
            YucaProfile.TasksActivities.newBuilder()
                .setTimestamp(timestamp)
                .addAllActivity(profiler.activity.sample(tick).values()));
        profile.addTaskPower(
            YucaProfile.TasksPowers.newBuilder()
                .setTimestamp(timestamp)
                .addAllPower(profiler.taskPower.sample(tick).values()));
        profile.addTaskEmissions(
            YucaProfile.TasksEmissionsRates.newBuilder()
                .setTimestamp(timestamp)
                .addAllEmissions(profiler.taskEmissions.sample(tick).values()));
      }

      try (DataOutputStream out = new DataOutputStream(new FileOutputStream(OUTPUT))) {
        profile.build().writeTo(out);
      } catch (Exception e) {

      }
  }

    private ProfilerUtil() {}
}
