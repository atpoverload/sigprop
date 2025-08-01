package yuca.profiler.linux.freq;

import static charcoal.util.LoggerUtil.getLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import yuca.profiler.linux.CpuFrequency;

/**
 * A simple (unsafe) wrapper for reading the dvfs system. Consult
 * https://www.kernel.org/doc/html/v4.14/admin-guide/pm/cpufreq.html for more details.
 */
public final class CpuFreq {
  private static final Logger logger = getLogger();

  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final Path SYS_CPU = Paths.get("/sys", "devices", "system", "cpu");

  /** Returns the expected frequency in Hz of a cpu. */
  public static int getFrequency(int cpu) {
    return 1000 * readCounter(cpu, "cpuinfo_cur_freq");
  }

  /** Returns the observed frequency in Hz of a cpu. */
  public static int getObservedFrequency(int cpu) {
    return 1000 * readCounter(cpu, "scaling_cur_freq");
  }

  /** Returns the current governor of a cpu. */
  public static String getGovernor(int cpu) {
    return readFromComponent(cpu, "scaling_governor");
  }

  public static Map<Integer, CpuFrequency> sampleCpuFrequencies() {
    HashMap<Integer, CpuFrequency> frequencies = new HashMap<>();
    for (int cpu = 0; cpu < CPU_COUNT; cpu++) {
      frequencies.put(
          cpu,
          CpuFrequency.newBuilder()
              .setCpu(cpu)
              .setGovernor(getGovernor(cpu))
              .setFrequency(getObservedFrequency(cpu))
              .setSetFrequency(getFrequency(cpu))
              .build());
    }
    return frequencies;
  }

  private static int readCounter(int cpu, String component) {
    String counter = readFromComponent(cpu, component);
    if (counter.isBlank()) {
      return 0;
    }
    try {
      return Integer.parseInt(counter.strip());
    } catch (Exception e) {
      logger.log(
          Level.WARNING,
          String.format("unable to sample from %s", getComponentPath(cpu, component)),
          e);
      return 0;
    }
  }

  private static synchronized String readFromComponent(int cpu, String component) {
    try {
      return Files.readString(getComponentPath(cpu, component));
    } catch (Exception e) {
      // e.printStackTrace();
      return "";
    }
  }

  private static Path getComponentPath(int cpu, String component) {
    return Paths.get(SYS_CPU.toString(), String.format("cpu%d", cpu), "cpufreq", component);
  }

  private CpuFreq() {}
}
