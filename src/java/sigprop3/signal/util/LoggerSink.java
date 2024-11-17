package sigprop3.signal.util;

import java.time.Instant;
import java.util.logging.Logger;
import sigprop3.SinkSignal;
import sigprop3.SourceSignal;
import sigprop3.util.LoggerUtil;

/** A {@link SinkSignal} that prints a {@link SourceSignal} to a logger. */
public final class LoggerSink implements SinkSignal {
  public static LoggerSink forSigprop(SourceSignal<?> first) {
    return new LoggerSink(first, LoggerUtil.getLogger());
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
    logger.info(String.format("%s@%s", timestamp, source.sample(timestamp)));
  }
}
