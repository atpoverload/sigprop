package charcoal.util;

import java.time.Duration;
import java.time.Instant;

/** Utilities for algebra with {@link Instants} and {@link Durations}. */
public final class Timestamps {
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
      // TODO: Remember to fix this when we migrate the files over to /src/jcarbon.
      NativeUtils.loadLibraryFromJar("/c/charcoal/util/libtime.so");
      return true;
    } catch (Exception e) {
      LoggerUtil.getLogger().info("couldn't load native timestamps library");
      e.printStackTrace();
      return false;
    }
  }

  static {
    HAS_NATIVE = loadLibrary();
  }

  private Timestamps() {}
}
