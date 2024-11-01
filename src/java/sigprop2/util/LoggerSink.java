package sigprop2.util;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Logger;
import sigprop2.SinkSignal;
import sigprop2.SourceSignal;

public final class LoggerSink implements SinkSignal {
  private static final Logger logger = LoggerUtil.getLogger();

  private final ArrayList<SourceSignal<?>> sources = new ArrayList<>();

  public LoggerSink(SourceSignal<?> source) {
    this.sources.add(source);
  }

  public LoggerSink(SourceSignal<?> first, SourceSignal<?>... others) {
    this.sources.add(first);
    for (SourceSignal<?> source : others) {
      this.sources.add(source);
    }
  }

  @Override
  public void update(Instant timestamp) {
    logger.info(
        String.format(
            "%s@%s",
            timestamp, sources.stream().map(source -> source.sample(timestamp)).collect(toList())));
  }
}
