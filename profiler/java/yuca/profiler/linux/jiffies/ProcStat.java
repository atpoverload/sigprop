package yuca.profiler.linux.jiffies;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Helper for reading system jiffies from /proc system. Refer to
 * https://man7.org/linux/man-pages/man5/proc.5.html
 */
public final class ProcStat {
  private static final Logger logger = getLogger("proc-task");

  // system information
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final String SYSTEM_STAT_FILE = "/proc/stat";

  // indicies for cpu stat because there are so many
  private enum CpuIndex {
    CPU(0),
    USER(1),
    NICE(2),
    SYSTEM(3),
    IDLE(4),
    IOWAIT(5),
    IRQ(6),
    SOFTIRQ(7),
    STEAL(8),
    GUEST(9),
    GUEST_NICE(10);

    private int index;

    private CpuIndex(int index) {
      this.index = index;
    }
  }

  public static Map<Integer, CpuJiffies> sampleCpuJiffies() {
    String[] stats = new String[0];
    // TODO: using the traditional java method to support android
    try {
      BufferedReader reader = new BufferedReader(new FileReader(SYSTEM_STAT_FILE));
      stats = readCpus(reader);
      reader.close();
    } catch (Exception e) {
      logger.info("unable to read " + SYSTEM_STAT_FILE);
    }

    return parseCpus(stats);
  }

  public static Map<Integer, CpuJiffiesRate> difference(
      Instant start, Instant end, Map<Integer, CpuJiffies> first, Map<Integer, CpuJiffies> second) {
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException(
          String.format(
              "timestamps have non-positive elapsed duration (%s - %s = %s)",
              end, start, Duration.between(end, start)));
    }
    if (!first.keySet().equals(second.keySet())) {
      throw new IllegalArgumentException(
          String.format(
              "data does not have the same number of cpus (%s != %s)",
              first.size(), second.size()));
    }
    return first.keySet().stream()
        .map(cpu -> ProcStat.difference(start, end, first.get(cpu), second.get(cpu)))
        .collect(toMap(cpu -> cpu.getCpu(), cpu -> cpu));
  }

  public static CpuJiffiesRate difference(
      Instant start, Instant end, CpuJiffies first, CpuJiffies second) {
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException(
          String.format(
              "timestamps have non-positive elapsed duration (%s - %s = %s)",
              end, start, Duration.between(end, start)));
    }
    if (first.getCpu() != second.getCpu()) {
      throw new IllegalArgumentException(
          String.format(
              "jiffies are not from the same cpu (%s != %s)", first.getCpu(), second.getCpu()));
    }
    long jiffies = second.getJiffies() - first.getJiffies();
    double elapsed = Duration.between(end, start).toNanos() / 1000000000.0;
    return CpuJiffiesRate.newBuilder().setCpu(first.getCpu()).setRate(jiffies / elapsed).build();
  }

  /** Reads the system's stat file and returns individual cpus. */
  private static String[] readCpus(BufferedReader reader) throws Exception {
    String[] stats = new String[CPU_COUNT];
    reader.readLine(); // first line is total summary; we need by cpu
    for (int i = 0; i < CPU_COUNT; i++) {
      stats[i] = reader.readLine();
    }
    return stats;
  }

  /** Turns stat strings into a {@link CpuSample}. */
  private static Map<Integer, CpuJiffies> parseCpus(String[] stats) {
    HashMap<Integer, CpuJiffies> readings = new HashMap<>();
    for (int i = 0; i < stats.length; i++) {
      String[] stat = stats[i].split(" ");
      if (stat.length != 11) {
        continue;
      }
      CpuJiffies.Builder jiffies =
          CpuJiffies.newBuilder()
              .setCpu(Integer.parseInt(stat[CpuIndex.CPU.index].substring(3)))
              .setUser(Integer.parseInt(stat[CpuIndex.USER.index]))
              .setNice(Integer.parseInt(stat[CpuIndex.NICE.index]))
              .setSystem(Integer.parseInt(stat[CpuIndex.SYSTEM.index]))
              .setIdle(Integer.parseInt(stat[CpuIndex.IDLE.index]))
              .setIowait(Integer.parseInt(stat[CpuIndex.IOWAIT.index]))
              .setIrq(Integer.parseInt(stat[CpuIndex.IRQ.index]))
              .setSoftirq(Integer.parseInt(stat[CpuIndex.SOFTIRQ.index]))
              .setSteal(Integer.parseInt(stat[CpuIndex.STEAL.index]))
              .setGuest(Integer.parseInt(stat[CpuIndex.GUEST.index]))
              .setGuestNice(Integer.parseInt(stat[CpuIndex.GUEST_NICE.index]));
      jiffies.setJiffies(
          jiffies.getUser()
              + jiffies.getNice()
              + jiffies.getSystem()
              + jiffies.getIowait()
              + jiffies.getIrq()
              + jiffies.getSoftirq()
              + jiffies.getSteal()
              + jiffies.getGuest()
              + jiffies.getGuestNice());
      readings.put(jiffies.getCpu(), jiffies.build());
    }
    return readings;
  }

  private ProcStat() {}
}
