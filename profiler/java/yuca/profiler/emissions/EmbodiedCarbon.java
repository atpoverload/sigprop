package yuca.profiler.emissions;

public final class EmbodiedCarbon {
  public static double SYSTEM_EMBODIED_CARBON = getSystemEmbodiedCarbon();

  public static double getSystemEmbodiedCarbon() {
    try {
      return Double.parseDouble(System.getProperty("yuca.profiler.emissions.embodied.system"));
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private EmbodiedCarbon() {}
}
