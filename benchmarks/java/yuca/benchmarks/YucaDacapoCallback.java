package yuca.benchmarks;

import static yuca.benchmarks.BenchmarkHelper.createOutputPath;
import static yuca.benchmarks.BenchmarkHelper.createProfiler;
import static yuca.benchmarks.BenchmarkHelper.dumpProfile;

import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;
import yuca.profiler.YucaProfiler;

public class YucaDacapoCallback extends Callback {
  private final YucaProfiler profiler = createProfiler(10);
  private int iteration = 0;

  public YucaDacapoCallback(CommandLineArgs args) {
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
    dumpProfile(profiler.getProfile(), createOutputPath("dacapo", benchmark, iteration));
    iteration++;
  }
}
