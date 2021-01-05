package sandbox.dgs.processor;

import org.reactivestreams.Publisher;

public interface IProcessor<T> {
    /**
     * Publisher of an event emitted.
     */
    Publisher<T> publish();
    /**
     * Emit an event.
     */
    void emit(T t);
}
