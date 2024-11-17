package sigprop3.signal.sync;

import sigprop3.SinkSignal;

/** A {@link SynchronousSignal} that is a sink. This exists for user convenience. */
public abstract class SynchronousProcessingSignal<T> extends SynchronousSignal<T>
    implements SinkSignal {}
