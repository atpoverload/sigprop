package yuca.profiler.emissions;

import static charcoal.util.LoggerUtil.getLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class EmbodiedCarbon {
  private static final Logger logger = getLogger("yuca-embodied-carbon");
  private static final String EMBODIED_CARBON_PROPERTY = "yuca.profiler.emissions.embodied.system";

  public static double SYSTEM_EMBODIED_CARBON = getSystemEmbodiedCarbon();

  public static double getSystemEmbodiedCarbon() {
    try {
      String embodiedCarbon = System.getProperty(EMBODIED_CARBON_PROPERTY);
      logger.info(String.format("checking %s=%s", EMBODIED_CARBON_PROPERTY, embodiedCarbon));
      return Double.parseDouble(embodiedCarbon);
    } catch (NumberFormatException | NullPointerException e) {
      logger.log(Level.WARNING, "unable to determine the system's embodied carbon", e);
      return 0.0;
    }
  }

  private EmbodiedCarbon() {}
}
