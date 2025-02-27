package charcoal.profiler.linux.freq;

import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;

public final class CpuFrequencySignal extends GeneratingSignal<Map<Integer, CpuFrequency>> {
  public CpuFrequencySignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, CpuFrequency> compute() {
    return CpuFreq.sampleFrequencies();
  }
}
