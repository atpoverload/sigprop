package yuca.profiler;

public interface Profiler {
  void start();

  void stop();

  YucaProfile getProfile();
}
