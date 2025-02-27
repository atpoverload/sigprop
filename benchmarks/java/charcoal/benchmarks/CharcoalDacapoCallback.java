package charcoal.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;

import charcoal.profiler.CharcoalProfile;
import charcoal.profiler.CharcoalProfiler;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;

public class CharcoalDacapoCallback extends Callback {
  private static final String OUTPUT =
      System.getProperty(
          "charcoal.benchmarks.output",
          String.format("/tmp/dacapo-%d-report.pb", ProcessHandle.current().pid()));
  private static final Logger logger = getLogger();

  private static final ScheduledExecutorService SAMPLING_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "charcoal-sampling-thread");
            t.setDaemon(true);
            return t;
          });

  private static final ScheduledExecutorService WORK_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "charcoal-worker-thread");
            t.setDaemon(true);
            return t;
          });

  private final CharcoalProfiler profiler =
      new CharcoalProfiler(Duration.ofMillis(10), SAMPLING_EXECUTOR, WORK_EXECUTOR);

  public CharcoalDacapoCallback(CommandLineArgs args) {
    super(args);
  }

  @Override
  public void start(String benchmark) {
    profiler.clock.start();
    super.start(benchmark);
  }

  @Override
  public void complete(String benchmark, boolean valid, boolean warmup) {
    super.complete(benchmark, valid, warmup);
    profiler.clock.stop();
  }

  @Override
  public boolean runAgain() {
    // if we have run every iteration, dump the data and terminate
    if (!super.runAgain()) {
      CharcoalProfile.Builder profile = CharcoalProfile.newBuilder();
      for (Instant tick : profiler.clock.ticks()) {
        CharcoalProfile.Timestamp timestamp =
            CharcoalProfile.Timestamp.newBuilder()
                .setSecs(tick.getEpochSecond())
                .setNanos(tick.getNano())
                .build();
        profile.addCpuFreq(
            CharcoalProfile.CpusFrequencies.newBuilder()
                .setTimestamp(timestamp)
                .addAllFrequency(profiler.freqs.sample(tick).values()));
        profile.addSocketPower(
            CharcoalProfile.SocketsPowers.newBuilder()
                .setTimestamp(timestamp)
                .addAllPower(profiler.socketPower.sample(tick).values()));
        profile.addSocketEmissions(
            CharcoalProfile.SocketsEmissionsRates.newBuilder()
                .setTimestamp(timestamp)
                .addAllEmissions(profiler.socketEmissions.sample(tick).values()));
        profile.addTaskActivity(
            CharcoalProfile.TasksActivities.newBuilder()
                .setTimestamp(timestamp)
                .addAllActivity(profiler.activity.sample(tick).values()));
        profile.addTaskPower(
            CharcoalProfile.TasksPowers.newBuilder()
                .setTimestamp(timestamp)
                .addAllPower(profiler.taskPower.sample(tick).values()));
        profile.addTaskEmissions(
            CharcoalProfile.TasksEmissionsRates.newBuilder()
                .setTimestamp(timestamp)
                .addAllEmissions(profiler.taskEmissions.sample(tick).values()));
      }

      try (DataOutputStream out = new DataOutputStream(new FileOutputStream(OUTPUT))) {
        profile.build().writeTo(out);
      } catch (Exception e) {

      }
      return false;
    } else {
      return true;
    }
  }
}
