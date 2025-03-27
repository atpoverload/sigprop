package yuca.profiler.linux.freq;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.logging.Logger;
import yuca.profiler.linux.CpuFrequency;

/** Very simple energy monitor that reports energy consumption over 10 millisecond intervals. */
final class CpuFreqMonitor {
  private static final Logger logger = getLogger();

  public static void main(String[] args) throws Exception {
    while (true) {
      Thread.sleep(10);
      logger.info(
          String.format(
              "%s",
              CpuFreq.sampleFrequencies().values().stream()
                  .sorted(comparing(CpuFrequency::getCpu))
                  .map(
                      freq ->
                          String.format(
                              "%d: %d(%d)",
                              freq.getCpu(), freq.getFrequency(), freq.getSetFrequency()))
                  .collect(toList())));
    }
  }
}
