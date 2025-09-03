package charcoal.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** Utility for a general logger. Should only be used by owners of this codebase. */
public final class LoggerUtil {
  private static final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");
  private static final HashSet<String> initializedLoggers = new HashSet<>();

  private static String makePrefix(String loggerName, Date date) {
    return String.join(
        " ",
        "(" + dateFormatter.format(date) + ")",
        "[" + Thread.currentThread().getName() + "]",
        "<" + loggerName + ">:");
  }

  /** (Sets up) and grabs a pre-configured logger that is verbose enough for debugging. */
  public static Logger getLogger(String loggerName) {
    if (!initializedLoggers.contains(loggerName)) {
      synchronized (LoggerUtil.class) {
        if (!initializedLoggers.contains(loggerName)) {
          ConsoleHandler handler = new ConsoleHandler();
          handler.setFormatter(
              new Formatter() {
                @Override
                public String format(LogRecord record) {
                  return String.join(
                      " ",
                      makePrefix(loggerName, new Date(record.getMillis())),
                      record.getMessage(),
                      System.lineSeparator());
                }
              });

          Logger logger = Logger.getLogger(loggerName);
          logger.setUseParentHandlers(false);

          for (Handler hdlr : logger.getHandlers()) {
            logger.removeHandler(hdlr);
          }
          logger.addHandler(handler);

          initializedLoggers.add(loggerName);
          return logger;
        }
      }
    }
    return Logger.getLogger(loggerName);
  }

  private LoggerUtil() {}
}
