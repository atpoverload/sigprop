package yuca.benchmarks;

import static yuca.benchmarks.BenchmarkHelper.createOutputPath;
import static yuca.benchmarks.BenchmarkHelper.createProfiler;
import static yuca.benchmarks.BenchmarkHelper.dumpProfile;

import org.renaissance.Plugin;
import yuca.profiler.YucaProfiler;

public final class YucaRenaissancePlugin
    implements Plugin.AfterOperationSetUpListener, Plugin.BeforeOperationTearDownListener {
  private final YucaProfiler profiler = createProfiler(10);

  @Override
  public void afterOperationSetUp(String benchmark, int opIndex, boolean isLastOp) {
    profiler.clock.start();
  }

  @Override
  public void beforeOperationTearDown(String benchmark, int opIndex, long durationNanos) {
    profiler.clock.stop();
    dumpProfile(profiler.getProfile(), createOutputPath("dacapo", benchmark, opIndex));
  }
}
