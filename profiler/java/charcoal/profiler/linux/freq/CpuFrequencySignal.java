package charcoal.profiler.linux.freq;

import charcoal.profiler.units.Frequency;
import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;

public final class CpuFrequencySignal extends GeneratingSignal<Map<Integer, CpuFrequency>>
    implements Frequency {
  public CpuFrequencySignal(Executor executor) {
    super(executor);
  }

  @Override
  protected Map<Integer, CpuFrequency> generate() {
    System.out.println("???");
    return CpuFreq.sampleFrequencies();
  }
}
