package sigprop.util;

import java.time.Duration;
import java.time.Instant;

/** Utilities for algebra with {@link Instants} and {@link Durations}. */
public final class Timestamps {
  /** Returns the minimum (i.e. furthest in the past) {@link Instant}. */
  public static Instant earliest(Instant first, Instant second) {
    if (first.isBefore(second)) {
      return first;
    } else {
      return second;
    }
  }

  /** Returns the maximum (i.e. furthest in the future) {@link Instant}. */
  public static Instant latest(Instant first, Instant second) {
    if (first.isAfter(second)) {
      return first;
    } else {
      return second;
    }
  }

  /** Returns the minimum (i.e. furthest in the past) {@link Instant}. */
  public static Instant earliest(Instant first, Instant... others) {
    Instant timestamp = first;
    for (Instant other : others) {
      timestamp = earliest(timestamp, other);
    }
    return timestamp;
  }

  /** Returns the maximum (i.e. furthest in the future) {@link Instant}. */
  public static Instant latest(Instant first, Instant... others) {
    Instant timestamp = first;
    for (Instant other : others) {
      timestamp = latest(timestamp, other);
    }
    return timestamp;
  }

  /** Computes the difference in seconds between two timestamps. */
  public static double betweenAsSecs(Instant first, Instant second) {
    return (double) Duration.between(first, second).toNanos() / 1000000000.0;
  }

  /**
   * Computes the ratio of elapsed time between two {@link Durations}. It is recommended that the
   * {@code dividend} is less than the {@code divisor} otherwise the value is somewhat non-sensical.
   */
  public static double divide(Duration dividend, Duration divisor) {
    return (double) (dividend.toNanos()) / (double) (divisor.toNanos());
  }

  // Native methods
  private static final boolean HAS_NATIVE;

  /** Returns the current time with microsecond precision if possible. */
  public static Instant now() {
    if (!HAS_NATIVE) {
      return Instant.now();
    }
    long timestamp = epochTimeNative();
    long secs = timestamp / 1000000;
    long micros = timestamp - 1000000 * secs;
    return Instant.ofEpochSecond(secs, 1000 * micros);
  }

  public static long monotonicTime() {
    if (!HAS_NATIVE) {
      return -1;
    }
    return monotonicTimeNative();
  }

  /** Returns the current unixtime in microseconds. */
  private static native long epochTimeNative();

  /** Returns the current monotonic time. */
  private static native long monotonicTimeNative();

  private static boolean loadLibrary() {
    try {
      // TODO: Remember to fix this when we migrate the files over to /src/jcarbon.
      NativeUtils.loadLibraryFromJar("/c/sigprop/util/libtime.so");
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
