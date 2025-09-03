package charcoal.prop.util;

import charcoal.SinkSignal;
import charcoal.SourceSignal;
import static charcoal.util.LoggerUtil.getLogger;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A {@link SinkSignal} that prints a @{link SourceSignal} to an updating console. */
public final class ConsoleSink implements SinkSignal {
  private static final Logger logger = getLogger("charcoal-console");

  private static final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");

  private static String makePrefix() {
    return String.join(
        " ",
        "charcoal-console",
        "(" + dateFormatter.format(Date.from(Instant.now())) + ")",
        "[" + Thread.currentThread().getName() + "]:");
  }

  public static Function<SourceSignal<?>, ConsoleSink> withOutputStream(OutputStream out) {
    return first -> new ConsoleSink(first, new PrintStream(out));
  }

  public static ConsoleSink withSystemOut(SourceSignal<?> source) {
    return new ConsoleSink(source, System.out);
  }

  private final SourceSignal<?> source;
  private final PrintStream out;

  public ConsoleSink(SourceSignal<?> source, PrintStream out) {
    this.source = source;
    this.out = out;
  }

  /** Prints the data at the given timestamp. */
  @Override
  public void update(Instant timestamp) {
    try {
      String message = String.format("%s %s@%s", makePrefix(), timestamp, source.sample(timestamp));
      out.print(message);
      out.print("\b".repeat(message.length()));
    } catch (Exception e) {
      logger.log(
          Level.SEVERE, String.format("failed to sample from %s at %s", source, timestamp), e);
      e.printStackTrace();
    }
  }
}
