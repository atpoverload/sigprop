package yuca.benchmarks;

import yuca.profiler.YucaProfiler;
import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;

public class YucaDacapoCallback extends Callback {
  private final YucaProfiler profiler = ProfilerUtil.newProfiler();
  
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
  }

  @Override
  public boolean runAgain() {
    // if we have run every iteration, dump the data and terminate
    if (!super.runAgain()) {
      ProfilerUtil.dumpProfile(profiler);
      return false;
    } else {
      return true;
    }
  }
}
