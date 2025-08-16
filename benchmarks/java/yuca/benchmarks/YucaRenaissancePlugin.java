package yuca.benchmarks;

import static yuca.benchmarks.BenchmarkHelper.createProfiler;
import static yuca.benchmarks.BenchmarkHelper.writeProfile;

import org.renaissance.Plugin;
import yuca.profiler.Profiler;
import yuca.profiler.YucaProfile;
import yuca.profiler.YucaSession.YucaSessionMetadata;

public final class YucaRenaissancePlugin
    implements Plugin.AfterOperationSetUpListener, Plugin.BeforeOperationTearDownListener {
  private Profiler profiler;

  @Override
  public void afterOperationSetUp(String benchmark, int opIndex, boolean isLastOp) {
    profiler = createProfiler();
    profiler.start();
  }

  @Override
  public void beforeOperationTearDown(String benchmark, int opIndex, long durationNanos) {
    profiler.stop();
    YucaProfile.Builder profile = profiler.getProfile().toBuilder();
    profiler = null;
    profile
        .getSessionBuilder()
        .addMetadata(YucaSessionMetadata.newBuilder().setKey("suite").setValue("renaissance"))
        .addMetadata(YucaSessionMetadata.newBuilder().setKey("benchmark").setValue(benchmark))
        .addMetadata(
            YucaSessionMetadata.newBuilder()
                .setKey("iteration")
                .setValue(Integer.toString(opIndex)));
    writeProfile(profile.build(), String.format("%s-%d", benchmark, opIndex));
  }
}
