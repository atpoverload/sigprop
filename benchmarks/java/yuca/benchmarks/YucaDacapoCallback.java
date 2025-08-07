package yuca.benchmarks;

import static yuca.benchmarks.BenchmarkHelper.createProfiler;
import static yuca.benchmarks.BenchmarkHelper.createTempOutputPath;
import static yuca.benchmarks.BenchmarkHelper.dumpProfile;

import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;
import yuca.profiler.Profiler;

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
    System.out.println(profiler.getProfile());
    dumpProfile(profiler.getProfile(), createTempOutputPath("dacapo", benchmark, iteration));
    iteration++;
  }
}
