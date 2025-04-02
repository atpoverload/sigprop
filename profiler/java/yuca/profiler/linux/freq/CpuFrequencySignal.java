package yuca.profiler.linux.freq;

import java.util.Map;
import java.util.concurrent.Executor;
import yuca.profiler.linux.CpuFrequency;
import charcoal.prop.GeneratingSignal;

public final class CpuFrequencySignal extends GeneratingSignal<Map<Integer, CpuFrequency>> {
  public CpuFrequencySignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, CpuFrequency> compute() {
    return CpuFreq.sampleFrequencies();
  }
}
