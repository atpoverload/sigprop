package yuca.benchmarks;

import static yuca.benchmarks.BenchmarkHelper.createProfiler;
import static yuca.benchmarks.BenchmarkHelper.createTempOutputPath;
import static yuca.benchmarks.BenchmarkHelper.dumpProfile;

import org.renaissance.Plugin;
import yuca.profiler.YucaProfiler;

public final class YucaRenaissancePlugin
    implements Plugin.AfterOperationSetUpListener, Plugin.BeforeOperationTearDownListener {
  private YucaProfiler profiler;

  @Override
  public void afterOperationSetUp(String benchmark, int opIndex, boolean isLastOp) {
    profiler = createProfiler(100);
    profiler.clock.start();
  }

  @Override
  public void beforeOperationTearDown(String benchmark, int opIndex, long durationNanos) {
    profiler.clock.stop();
    dumpProfile(profiler.getProfile(), createTempOutputPath("dacapo", benchmark, opIndex));
  }
}
