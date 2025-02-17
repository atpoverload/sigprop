package charcoal.profiler.linux.jiffies;

import charcoal.prop.GeneratingSignal;
import java.util.Map;
import java.util.concurrent.Executor;

public final class TaskJiffiesSignal extends GeneratingSignal<Map<Long, TaskJiffies>>
    implements Jiffies {
  private static final long PID = ProcessHandle.current().pid();

  public static TaskJiffiesSignal current(Executor executor) {
    return new TaskJiffiesSignal(PID, executor);
  }

  private final long pid;

  public TaskJiffiesSignal(long pid, Executor executor) {
    super(executor);
    this.pid = pid;
  }

  @Override
  protected Map<Long, TaskJiffies> compute() {
    return ProcTask.sampleTasksFor(pid);
  }
}
