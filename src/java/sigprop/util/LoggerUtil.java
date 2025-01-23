package sigprop.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** Utility for a general logger. Should only be used by owners of this codebase. */
public final class LoggerUtil {
  private static final String LOGGER_NAME = "sigprop";

  private static final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");

  private static boolean isInitialized = false;

  private static String makePrefix(Date date) {
    return String.join(
        " ",
        LOGGER_NAME,
        "(" + dateFormatter.format(date) + ")",
        "[" + Thread.currentThread().getName() + "]:");
  }

  /** (Sets up) and grabs a pre-configured logger that is verbose enough for debugging. */
  public static Logger getLogger() {
    if (!isInitialized) {
      synchronized (LoggerUtil.class) {
        if (!isInitialized) {
          ConsoleHandler handler = new ConsoleHandler();
          handler.setFormatter(
              new Formatter() {
                @Override
                public String format(LogRecord record) {
                  return String.join(
                      " ",
                      makePrefix(new Date(record.getMillis())),
                      record.getMessage(),
                      System.lineSeparator());
                }
              });

          Logger logger = Logger.getLogger(LOGGER_NAME);
          logger.setUseParentHandlers(false);

          for (Handler hdlr : logger.getHandlers()) {
            logger.removeHandler(hdlr);
          }
          logger.addHandler(handler);

          return logger;
        }
      }
    }
    return Logger.getLogger(LOGGER_NAME);
  }

  private LoggerUtil() {}
}
