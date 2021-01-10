package sandbox.dgs.processor;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import sandbox.dgs.model.Book;

@Component
public class BookInMemoryProcessor implements IBookProcessor {
    private final Sinks.Many<Book> sinksOne = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Publisher<Book> publish() {
        return sinksOne.asFlux().publishOn(Schedulers.single());
    }

    @Override
    public void emit(Book book) {
        sinksOne.tryEmitNext(book);
    }
}
