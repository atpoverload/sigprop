package yuca.benchmarks;

import static yuca.benchmarks.BenchmarkHelper.createProfiler;
import static yuca.benchmarks.BenchmarkHelper.dumpProfile;

import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;
import yuca.profiler.Profiler;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaSession.YucaSessionMetadata;

public class YucaDacapoCallback extends Callback {
  private Profiler profiler;
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
        .addMetadata(YucaSessionMetadata.newBuilder().setKey("case").setValue(""))
        .addMetadata(YucaSessionMetadata.newBuilder().setKey("suite").setValue("dacapo"))
        .addMetadata(YucaSessionMetadata.newBuilder().setKey("benchmark").setValue(benchmark))
        .addMetadata(
            YucaSessionMetadata.newBuilder()
                .setKey("iteration")
                .setValue(Integer.toString(iteration)));
    dumpProfile(profile.build());
    iteration++;
  }
}
