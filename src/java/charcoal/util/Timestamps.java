package charcoal.util;

import static charcoal.util.LoggerUtil.getLogger;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Utilities for algebra with {@link Instants} and {@link Durations}. */
public final class Timestamps {
  private static final Logger logger = getLogger("charcoal-timestamps");

  private static final String NATIVE_LIBRARY_PATH = "/c/charcoal/util/libtime.so";

  /**
   * Computes the ratio of elapsed time between two {@link Durations}. It is recommended that the
   * {@code dividend} is less than the {@code divisor} otherwise the value is somewhat non-sensical.
   */
  public static double divide(Duration dividend, Duration divisor) {
    return (double) dividend.toNanos() / divisor.toNanos();
  }

  // Native methods
  private static final boolean HAS_NATIVE;

  /** Returns a java {@link Instant} of the current unixtime with microsecond precision. */
  public static Instant now() {
    if (!HAS_NATIVE) {
      return Instant.now();
    }
    long timestamp = epochTimeNative();
    long secs = timestamp / 1000000;
    long micros = timestamp - 1000000 * secs;
    return Instant.ofEpochSecond(secs, 1000 * micros);
  }

  /** Returns the current monotonic clock time in nanoseconds. */
  public static long monotonicTime() {
    if (!HAS_NATIVE) {
      return 0;
    }
    return monotonicTimeNative();
  }

  /** Returns the unixtime as microseconds. */
  private static native long epochTimeNative();

  /** Returns the monotonic clock time as nanoseconds. */
  private static native long monotonicTimeNative();

  private static boolean loadLibrary() {
    try {
      logger.info(String.format("loading native timestamps library from %s", NATIVE_LIBRARY_PATH));
      NativeUtils.loadLibraryFromJar(NATIVE_LIBRARY_PATH);
      return true;
    } catch (IOException e) {
      logger.log(Level.WARNING, "couldn't load native timestamps library", e);
      return false;
    }
  }

  static {
    HAS_NATIVE = loadLibrary();
  }

  private Timestamps() {}
}
