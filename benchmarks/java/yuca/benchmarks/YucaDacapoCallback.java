package yuca.benchmarks;

import static yuca.benchmarks.YucaBenchmarkModule.createProfiler;
import static yuca.benchmarks.YucaBenchmarkModule.writeProfile;

import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaProfiler;
import yuca.profiler.YucaSession.YucaSessionMetadata;

public class YucaDacapoCallback extends Callback {
  private YucaProfiler profiler;
  private int iteration = 0;

  public YucaDacapoCallback(CommandLineArgs args) {
    super(args);
  }

  @Override
  public void start(String benchmark) {
    profiler = createProfiler();
    profiler.start();
    super.start(benchmark);
  }

  @Override
  public void complete(String benchmark, boolean valid, boolean warmup) {
    super.complete(benchmark, valid, warmup);
    profiler.stop();
    YucaProfile.Builder profile = profiler.getProfile().toBuilder();
    profiler = null;
    profile
        .getSessionBuilder()
        .addMetadata(YucaSessionMetadata.newBuilder().setKey("suite").setValue("dacapo"))
        .addMetadata(YucaSessionMetadata.newBuilder().setKey("benchmark").setValue(benchmark))
        .addMetadata(
            YucaSessionMetadata.newBuilder()
                .setKey("iteration")
                .setValue(Integer.toString(iteration)));
    writeProfile(profile.build(), String.format("%s-%d", benchmark, iteration));
    iteration++;
  }
}
