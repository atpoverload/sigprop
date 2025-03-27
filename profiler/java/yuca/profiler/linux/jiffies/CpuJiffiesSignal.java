package yuca.profiler.linux.jiffies;

import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;

public final class CpuJiffiesSignal extends GeneratingSignal<Map<Integer, CpuJiffies>> {
  public CpuJiffiesSignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, CpuJiffies> compute() {
    return ProcStat.sampleCpus();
  }
}
