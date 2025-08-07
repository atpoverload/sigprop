package yuca.benchmarks;

import static yuca.benchmarks.BenchmarkHelper.createProfiler;
import static yuca.benchmarks.BenchmarkHelper.createTempOutputPath;
import static yuca.benchmarks.BenchmarkHelper.dumpProfile;

import org.renaissance.Plugin;
import yuca.profiler.Profiler;

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
    dumpProfile(profiler.getProfile(), createTempOutputPath("renaissance", benchmark, opIndex));
  }
}
