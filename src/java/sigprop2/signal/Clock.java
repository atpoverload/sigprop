package sigprop2.signal;

import java.time.Instant;
import sigprop2.SourceSignal;

public interface Clock extends SourceSignal<Instant> {
  void start();

  void stop();
}
