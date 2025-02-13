package charcoal.profiler.linux.jiffies;

import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;

public final class CpuJiffiesSignal extends GeneratingSignal<Map<Integer, CpuJiffies>>
    implements Jiffies {
  public CpuJiffiesSignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, CpuJiffies> generate() {
    return ProcStat.sampleCpus();
  }
}
