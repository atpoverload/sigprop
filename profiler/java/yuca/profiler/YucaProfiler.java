package yuca.profiler;

public interface YucaProfiler {
  void start();

  void stop();

  YucaProfile getProfile();
}
