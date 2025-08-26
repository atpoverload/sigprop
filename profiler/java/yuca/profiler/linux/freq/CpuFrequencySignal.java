package yuca.profiler.linux.freq;

import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;
import yuca.profiler.linux.CpuFrequency;

public final class CpuFrequencySignal extends GeneratingSignal<Map<Integer, CpuFrequency>> {
  public CpuFrequencySignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, CpuFrequency> compute() {
    var freqs = CpuFreq.sampleCpuFrequencies();
    // System.out.println(String.format("freqs %s", freqs));
    // System.out.println(String.format("freq %s", freqs.get(0).getFrequency()));
    return freqs;
  }
}
