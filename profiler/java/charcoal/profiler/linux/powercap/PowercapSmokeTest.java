package charcoal.profiler.linux.powercap;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.stream.Collectors.joining;

import charcoal.profiler.linux.SocketPower;
import charcoal.util.Timestamps;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/** A smoke test to check which components are available and if they are reporting similarly. */
final class PowercapSmokeTest {
  private static final Logger logger = getLogger();

  private static int fib(int n) {
    if (n == 0 || n == 1) {
      return 1;
    } else {
      return fib(n - 1) + fib(n - 2);
    }
  }

  private static void exercise() {
    fib(42);
  }

  /** Checks if powercap is available for sampling. */
  private static boolean powercapAvailable() throws Exception {
    if (Powercap.SOCKETS < 1) {
      logger.info("system has no energy domains through powercap!");
      return false;
    }

    Map<Integer, PowercapEnergy> sample = Powercap.sample();
    Instant start = Timestamps.now();

    exercise();

    Instant end = Timestamps.now();
    Map<Integer, SocketPower> power = Powercap.difference(start, end, sample, Powercap.sample());

    if (power.values().stream().mapToDouble(SocketPower::getPower).sum() == 0) {
      logger.info("no energy consumed with the difference of two powercap samples!");
      return false;
    }

    logger.info(
        String.join(
            System.lineSeparator(),
            "powercap report",
            String.format(
                " - elapsed time: %.6fs",
                (double) Duration.between(start, end).toNanos() / 1000000000),
            IntStream.range(0, Powercap.SOCKETS)
                .mapToObj(
                    socket ->
                        String.format(
                            " - socket: %d, energy: %.6fJ",
                            socket + 1, power.get(socket).getPower()))
                .collect(joining(System.lineSeparator()))));
    return true;
  }

  public static void main(String[] args) throws Exception {
    logger.info("warming up...");
    for (int i = 0; i < 5; i++) exercise();
    logger.info("testing powercap...");
    if (powercapAvailable()) {
      logger.info("all smoke tests passed!");
    } else {
      logger.info("smoke testing failed; please consult the log.");
    }
  }
}
