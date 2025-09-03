package charcoal.prop.util;

import charcoal.SinkSignal;
import charcoal.SourceSignal;
import static charcoal.util.LoggerUtil.getLogger;
import java.time.Instant;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A {@link SinkSignal} that prints a {@link SourceSignal} to a logger. */
public final class LoggerSink implements SinkSignal {
  public static Function<SourceSignal<?>, LoggerSink> withLogger(Logger logger) {
    return first -> new LoggerSink(first, logger);
  }

  public static LoggerSink withCharcoalLogger(SourceSignal<?> first) {
    return new LoggerSink(first, getLogger("charcoal-logger"));
  }

  private final SourceSignal<?> source;
  private final Logger logger;

  public LoggerSink(SourceSignal<?> source, Logger logger) {
    this.source = source;
    this.logger = logger;
  }

  /** Logs the data at the given timestamp. */
  @Override
  public void update(Instant timestamp) {
    try {
      source.sample(timestamp);
      logger.info(String.format("%s@%s", timestamp, source.sample(timestamp)));
    } catch (Exception e) {
      logger.log(
          Level.SEVERE, String.format("failed to sample from %s at %s", source, timestamp), e);
      e.printStackTrace();
    }
  }
}
