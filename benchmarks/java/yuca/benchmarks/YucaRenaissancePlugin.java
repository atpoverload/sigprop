package yuca.benchmarks;

import org.renaissance.Plugin;
import yuca.profiler.YucaProfiler;

public final class YucaRenaissancePlugin
    implements Plugin.BeforeBenchmarkTearDownListener,
        Plugin.AfterOperationSetUpListener,
        Plugin.BeforeOperationTearDownListener {
  private final YucaProfiler profiler = ProfilerUtil.newProfiler();

  @Override
  public void afterOperationSetUp(String benchmark, int opIndex, boolean isLastOp) {
    profiler.clock.start();
  }

  @Override
  public void beforeOperationTearDown(String benchmark, int opIndex, long durationNanos) {
    profiler.clock.stop();
  }

  @Override
  public void beforeBenchmarkTearDown(String benchmark) {
    ProfilerUtil.dumpProfile(profiler.getProfile());
  }
}
