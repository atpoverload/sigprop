package yuca.profiler.linux.powercap;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.stream.Collectors.toMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import yuca.profiler.linux.SocketPower;

/** Simple wrapper to read powercap's energy with pure Java. */
// TODO: this doesn't appear to work on more modern implementations that are hierarchical
public final class Powercap {
  private static final Logger logger = getLogger();

  private static final Path POWERCAP_ROOT =
      Paths.get("/sys", "devices", "virtual", "powercap", "intel-rapl");

  public static final int SOCKETS = getSocketCount();
  public static final double[][] MAX_ENERGY_JOULES = getMaximumEnergy();

  /** Returns whether we can read values. */
  public static boolean isAvailable() {
    return SOCKETS > 0;
  }

  /**
   * Returns an {@link PowercapSample} populated by parsing the string returned by {@ readNative}.
   */
  public static Map<Integer, PowercapEnergy> samplePowercap() {
    HashMap<Integer, PowercapEnergy> energy = new HashMap<>();
    for (int socket = 0; socket < SOCKETS; socket++) {
      double pkg = readPackage(socket);
      double dram = readDram(socket);
      energy.put(
          socket,
          PowercapEnergy.newBuilder()
              .setSocket(socket)
              .setEnergy(pkg + dram)
              .setPkg(pkg)
              .setDram(dram)
              .setCore(0)
              .setGpu(0)
              .build());
    }
    return energy;
  }

  /** Computes the difference of two {@link PowercapReadings}. */
  public static Map<Integer, SocketPower> difference(
      Instant start,
      Instant end,
      Map<Integer, PowercapEnergy> first,
      Map<Integer, PowercapEnergy> second) {
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException(
          String.format(
              "timestamps have non-positive elapsed duration (%s - %s = %s)",
              end, start, Duration.between(end, start)));
    }
    if (!first.keySet().equals(second.keySet())) {
      throw new IllegalArgumentException(
          String.format(
              "data does not have the same number of sockets (%s != %s)",
              first.size(), second.size()));
    }
    return first.keySet().stream()
        .map(socket -> Powercap.between(start, end, first.get(socket), second.get(socket)))
        .collect(toMap(socket -> socket.getSocket(), socket -> socket));
  }

  /** Computes the difference of two {@link PowercapReadings}. */
  public static SocketPower between(
      Instant start, Instant end, PowercapEnergy first, PowercapEnergy second) {
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException(
          String.format(
              "timestamps have non-positive elapsed duration (%s - %s = %s)",
              end, start, Duration.between(end, start)));
    }
    if (first.getSocket() != second.getSocket()) {
      throw new IllegalArgumentException(
          String.format(
              "readings are not from the same domain (%d != %d)",
              first.getSocket(), second.getSocket()));
    }
    double pkg = diffWithWraparound(first.getPkg(), second.getPkg(), first.getSocket(), 0);
    double dram = diffWithWraparound(first.getDram(), second.getDram(), first.getSocket(), 1);
    double elapsed = Duration.between(start, end).toNanos() / 1000000000.0;
    return SocketPower.newBuilder()
        .setSocket(first.getSocket())
        .setPower((pkg + dram) / elapsed)
        .build();
  }

  private static double diffWithWraparound(double first, double second, int socket, int component) {
    double energy = second - first;
    if (energy < 0) {
      logger.info(String.format("powercap overflow on %d:%d", socket, component));
      energy += MAX_ENERGY_JOULES[socket][component];
    }
    return energy;
  }

  private static int getSocketCount() {
    if (!Files.exists(POWERCAP_ROOT)) {
      logger.warning("couldn't check the socket count; powercap likely not available");
      return 0;
    }
    try {
      return (int)
          Files.list(POWERCAP_ROOT)
              .filter(p -> p.getFileName().toString().contains("intel-rapl"))
              .count();
    } catch (Exception e) {
      logger.warning("couldn't check the socket count; powercap likely not available");
      return 0;
    }
  }

  private static double[][] getMaximumEnergy() {
    if (!Files.exists(POWERCAP_ROOT)) {
      logger.warning("couldn't check the maximum energy; powercap likely not available");
      return new double[0][0];
    }
    // TODO: this is a hack and we need to formalize it
    try {
      double[][] maxEnergy =
          Files.list(POWERCAP_ROOT)
              .filter(p -> p.getFileName().toString().contains("intel-rapl"))
              .map(
                  socket -> {
                    double[] overflowValues = new double[2];
                    try {
                      overflowValues[0] =
                          Double.parseDouble(
                                  Files.readString(
                                      Path.of(socket.toString(), "max_energy_range_uj")))
                              / 1000000;
                    } catch (Exception e) {
                      logger.warning(
                          String.format("couldn't check the maximum energy for socket %s", socket));
                    }
                    try {
                      overflowValues[1] =
                          Double.parseDouble(
                                  Files.readString(
                                      Path.of(
                                          socket.toString(),
                                          String.format("%s:0", socket.getFileName()),
                                          "max_energy_range_uj")))
                              / 1000000;
                    } catch (Exception e) {
                      logger.warning(
                          String.format("couldn't check the maximum energy for socket %s", socket));
                    }
                    logger.info(
                        String.format(
                            "retrieved overflow values for %s: %s",
                            socket.getFileName(), Arrays.toString(overflowValues)));
                    return overflowValues;
                  })
              .toArray(double[][]::new);
      return maxEnergy;
    } catch (Exception e) {
      logger.warning("couldn't check the maximum energy; powercap likely not available");
      return new double[0][0];
    }
  }

  /**
   * Parses the contents of /sys/devices/virtual/powercap/intel-rapl/intel-rapl:<socket>/energy_uj,
   * which contains the number of microjoules consumed by the package since boot as an integer.
   */
  private static double readPackage(int socket) {
    String socketPrefix = String.format("intel-rapl:%d", socket);
    Path energyFile = Paths.get(POWERCAP_ROOT.toString(), socketPrefix, "energy_uj");
    try {
      return Double.parseDouble(Files.readString(energyFile)) / 1000000;
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Parses the contents of
   * /sys/devices/virtual/powercap/intel-rapl/intel-rapl:<socket>/intel-rapl:<socket>:0/energy_uj,
   * which contains the number of microjoules consumed by the dram since boot as an integer.
   */
  private static double readDram(int socket) {
    String socketPrefix = String.format("intel-rapl:%d", socket);
    Path energyFile =
        Paths.get(
            POWERCAP_ROOT.toString(),
            socketPrefix,
            String.format("%s:0", socketPrefix),
            "energy_uj");
    try {
      return Double.parseDouble(Files.readString(energyFile)) / 1000000;
    } catch (Exception e) {
      return 0;
    }
  }
}
