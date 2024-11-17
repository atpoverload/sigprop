package sigprop2.util;

import java.time.Instant;
import sigprop2.SinkSignal;
import sigprop2.SourceSignal;

public final class ConsoleSink implements SinkSignal {
  private final SourceSignal<?> source;

  public ConsoleSink(SourceSignal<?> source) {
    this.source = source;
  }

  @Override
  public void update(Instant timestamp) {
    String message = String.format("%s@%s", timestamp, source.sample(timestamp));
    System.out.print(message);
    System.out.print("\b".repeat(message.length()));
  }
}
