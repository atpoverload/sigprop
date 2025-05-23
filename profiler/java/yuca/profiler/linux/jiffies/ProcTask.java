package yuca.profiler.linux.jiffies;

import static charcoal.util.LoggerUtil.getLogger;
import static yuca.profiler.linux.CpuInfo.getCpuSocketMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import yuca.profiler.linux.Task;
import yuca.profiler.linux.TaskActivityRate;

/**
 * Helper for reading task jiffies from /proc system. Refer to
 * https://man7.org/linux/man-pages/man5/proc.5.html
 */
public final class ProcTask {
  private static final Logger logger = getLogger();

  private static final long PID = ProcessHandle.current().pid();
  private static final int[] SOCKETS_MAP = getCpuSocketMapping();
  // task stat indicies
  private static final int STAT_LENGTH = 52;

  private enum TaskIndex {
    TID(0),
    CPU(38),
    USER(13),
    SYSTEM(14);

    private int index;

    private TaskIndex(int index) {
      this.index = index;
    }
  };

  /** Reads from a process's tasks and returns a {@link Sample} of it. */
  public static Map<Long, TaskJiffies> sampleTaskJiffiesFor(long pid) {
    return parseTasks(readTasks(pid), pid);
  }

  /** Reads this process's tasks and returns a {@link Sample} of it. */
  public static Map<Long, TaskJiffies> sampleTaskJiffies() {
    return sampleTasksFor(PID);
  }

  public static Map<Long, TaskJiffiesRate> difference(
      Instant start, Instant end, Map<Long, TaskJiffies> first, Map<Long, TaskJiffies> second) {
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException(
          String.format(
              "timestamps have non-positive elapsed duration (%s - %s = %s)",
              end, start, Duration.between(end, start)));
    }
    HashMap<Long, TaskJiffiesRate> tasks = new HashMap<>();
    for (long tid : first.keySet()) {
      if (second.containsKey(tid)) {
        tasks.put(tid, ProcTask.difference(start, end, first.get(tid), second.get(tid)));
      }
    }
    return tasks;
  }

  public static TaskJiffiesRate between(
      Instant start, Instant end, TaskJiffies first, TaskJiffies second) {
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException(
          String.format(
              "timestamps have non-positive elapsed duration (%s - %s = %s)",
              end, start, Duration.between(end, start)));
    }
    double elapsed = Duration.between(end, start).toNanos() / 1000000000.0;
    int jiffies =
        Math.max(0, second.getUserJiffies() - first.getUserJiffies())
            + Math.max(0, second.getSystemJiffies() - first.getSystemJiffies());
    return TaskJiffiesRate.newBuilder()
        .setTask(first.getTask())
        .setCpu(first.getCpu())
        .setRate(jiffies / elapsed)
        .build();
  }

  /**
   * Computes the activity of all tasks in the overlapping region of two intervals by using the
   * ratio between a task's jiffies and cpu jiffies of the task's executing cpu. This also safely
   * bounds the value from 0 to 1, in the cases that the jiffies are misaligned due to the kernel
   * update timing.
   */
  // TODO: Need to find (or write) something that strictly mentions the timing issue
  public static Map<Long, TaskActivityRate> taskActivityRate(
      Map<Long, TaskJiffiesRate> tasks, Map<Integer, CpuJiffiesRate> cpus) {
    HashMap<Long, TaskActivityRate> activity = new HashMap<>();
    // Set this up to correct for kernel update.
    double[] totalJiffiesRate = new double[cpus.size()];
    for (TaskJiffiesRate task : tasks.values()) {
      int cpu = task.getCpu();
      totalJiffiesRate[cpu] += task.getRate();
    }
    for (TaskJiffiesRate task : tasks.values()) {
      // Don't bother if there are no jiffies.
      if (task.getRate() == 0) {
        continue;
      }
      // Correct for the kernel update by using total jiffies reported by tasks if the cpu
      // reported one is too small (this also catches zero jiffies reported by the cpu).
      int cpu = task.getCpu();
      double cpuJiffiesRate = Math.max(cpus.get(cpu).getRate(), totalJiffiesRate[cpu]);
      double taskActivity = Math.min(1.0, task.getRate() / cpuJiffiesRate);
      activity.put(
          task.getTask().getTaskId(),
          TaskActivityRate.newBuilder()
              .setTask(task.getTask())
              .setCpu(task.getCpu())
              .setActivity(taskActivity)
              .build());
    }
    return activity;
  }

  /** Reads stat files of tasks directory of a process. */
  private static final ArrayList<String> readTasks(long pid) {
    ArrayList<String> stats = new ArrayList<String>();
    File tasks = new File(String.join(File.separator, "/proc", Long.toString(pid), "task"));
    if (!tasks.exists()) {
      return stats;
    }

    for (File task : tasks.listFiles()) {
      File statFile = new File(task, "stat");
      if (!statFile.exists()) {
        continue;
      }
      // TODO: if a task terminates while we try to read it, we hang here
      // TODO: using the traditional java method to support android
      try {
        BufferedReader reader = new BufferedReader(new FileReader(statFile));
        stats.add(reader.readLine());
        reader.close();
      } catch (Exception e) {
        logger.info("unable to read task " + statFile + " before it terminated");
      }
    }
    return stats;
  }

  /** Turns task stat strings into {@link TaskJiffiesReadings}. */
  private static Map<Long, TaskJiffies> parseTasks(ArrayList<String> stats, long pid) {
    HashMap<Long, TaskJiffies> readings = new HashMap<>();
    for (String s : stats) {
      String[] stat = s.split(" ");
      if (stat.length >= STAT_LENGTH) {
        // task name can be space-delimited, so there may be extra entries
        int offset = stat.length - STAT_LENGTH;
        TaskJiffies task =
            TaskJiffies.newBuilder()
                .setTask(
                    Task.newBuilder()
                        .setProcessId(pid)
                        .setTaskId(Long.parseLong(stat[TaskIndex.TID.index]))
                        // TODO: the name is usually garbage unfortunately :/
                        .setName(getName(stat, offset)))
                .setCpu(Integer.parseInt(stat[TaskIndex.CPU.index + offset]))
                .setUserJiffies(Integer.parseInt(stat[TaskIndex.USER.index + offset]))
                .setSystemJiffies(Integer.parseInt(stat[TaskIndex.SYSTEM.index + offset]))
                .build();
        readings.put(task.getTask().getTaskId(), task);
      }
    }
    return readings;
  }

  /** Extracts the name from the stat string. */
  private static final String getName(String[] stat, int offset) {
    String name = String.join(" ", Arrays.copyOfRange(stat, 1, 2 + offset));
    return name.substring(1, name.length() - 1);
  }

  private ProcTask() {}
}
