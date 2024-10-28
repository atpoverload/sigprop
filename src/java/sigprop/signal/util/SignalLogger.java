package sigprop.signal.util;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Logger;
import sigprop.SinkSignal;
import sigprop.SourceSignal;
import sigprop.util.LoggerUtil;

/**
 * A {@link SinkSignal} that prints out the contents of all sources when updated. This sink stores
 * no data.
 */
public class SignalLogger implements SinkSignal<Void> {
  private static final Logger logger = LoggerUtil.getLogger();

  private final ArrayList<SourceSignal<? extends Object>> sources = new ArrayList<>();

  public SignalLogger(SourceSignal<? extends Object> source) {
    this.sources.add(source);
  }

  public SignalLogger(
      SourceSignal<? extends Object> source1, SourceSignal<? extends Object> source2) {
    this.sources.add(source1);
    this.sources.add(source2);
  }

  @Override
  public Void sample(Instant timestamp) {
    return null;
  }

  @Override
  public void update(Instant timestamp) {
    logger.info(
        String.format(
            "%s@%s",
            timestamp, sources.stream().map(source -> source.sample(timestamp)).collect(toList())));
  }
}
