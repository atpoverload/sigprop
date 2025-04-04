package yuca.profiler.linux.thermal;

import static charcoal.util.LoggerUtil.getLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import yuca.profiler.linux.ThermalZoneTemperature;

public class SysThermal {
  private static final Logger logger = getLogger();

  private static final Path SYS_THERMAL = Paths.get("/sys", "class", "thermal");
  private static final Map<Integer, String> ZONE_TYPES = getZones();

  public static final int ZONE_COUNT = getThermalZoneCount();

  public static int getTemperature(int zone) {
    if (zone < 0 || ZONE_COUNT <= zone) {
      throw new IllegalArgumentException(String.format("zone %d not available", zone));
    }
    return readCounter(zone, "temp") / 1000;
  }

  public static String getZoneType(int zone) {
    if (zone < 0 || ZONE_COUNT <= zone) {
      throw new IllegalArgumentException(String.format("zone %d not available", zone));
    }
    return ZONE_TYPES.get(zone);
  }

  public static Map<Integer, ThermalZoneTemperature> sampleThermalZones() {
    HashMap<Integer, ThermalZoneTemperature> zones = new HashMap<>();
    for (int zone = 0; zone < ZONE_COUNT; zone++) {
      zones.put(
          zone,
          ThermalZoneTemperature.newBuilder()
              .setZoneId(zone)
              .setZoneType(ZONE_TYPES.get(zone))
              .setTemperature(getTemperature(zone))
              .build());
    }
    return zones;
  }

  /**
   * Reads thermal zone information from /sys/class/thermal and returns the number of available
   * thermal zones.
   */
  private static int getThermalZoneCount() {
    if (!Files.exists(SYS_THERMAL)) {
      logger.warning("couldn't check the thermal zone count; thermal sysfs likely not available");
      return 0;
    }
    try {
      return (int)
          Files.list(SYS_THERMAL)
              .filter(p -> p.getFileName().toString().contains("thermal_zone"))
              .count();
    } catch (Exception e) {
      logger.warning("couldn't check the thermal zone count; thermal sysfs likely not available");
      return 0;
    }
  }

  /**
   * Reads thermal zone information from /sys/class/thermal/ and returns a map of each thermal zone
   * to its type.
   */
  private static Map<Integer, String> getZones() {
    if (!Files.exists(SYS_THERMAL)) {
      logger.warning("couldn't check the thermal zones; thermal sysfs likely not available");
      return Map.of();
    }
    try {
      return Files.list(SYS_THERMAL)
          .filter(p -> p.getFileName().toString().contains("thermal_zone"))
          .collect(
              Collectors.toMap(
                  p -> Integer.parseInt(p.toString().replaceAll("\\D+", "")),
                  p -> {
                    try {
                      return Files.readString(p.resolve("type")).toString().trim();
                    } catch (IOException e) {
                      logger.warning(
                          "couldn't read from /sys/class/thermal; thermal sysfs likely not"
                              + " available");
                      return "";
                    }
                  }));
    } catch (Exception e) {
      logger.warning("couldn't check the socket count; thermal sysfs likely not available");
      return Map.of();
    }
  }

  private static int readCounter(int cpu, String component) {
    String counter = readFromComponent(cpu, component);
    if (counter.isBlank()) {
      return 0;
    }
    return Integer.parseInt(counter);
  }

  private static synchronized String readFromComponent(int cpu, String component) {
    try {
      return Files.readString(getComponentPath(cpu, component)).trim();
    } catch (Exception e) {
      // e.printStackTrace();
      return "";
    }
  }

  private static Path getComponentPath(int zone, String component) {
    return Paths.get(SYS_THERMAL.toString(), String.format("thermal_zone%d", zone), component);
  }

  private SysThermal() {}
}
