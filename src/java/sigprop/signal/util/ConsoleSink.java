package sigprop.signal.util;

import java.io.PrintStream;
import java.time.Instant;
import sigprop.SinkSignal;
import sigprop.SourceSignal;

/** A {@link SinkSignal} that prints a @{link SourceSignal} to an updating console. */
public final class ConsoleSink implements SinkSignal {
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
    String message = String.format("%s@%s", timestamp, source.sample(timestamp));
    out.print(message);
    out.print("\b".repeat(message.length()));
  }
}
