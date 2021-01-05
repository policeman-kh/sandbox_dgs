package sandbox.dgs.processor;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import sandbox.dgs.model.Book;

@Component
public class BookInMemoryProcessor implements IBookProcessor {
    private final DirectProcessor<Book> processor = DirectProcessor.create();

    @Override
    public Publisher<Book> publish() {
        return processor.publishOn(Schedulers.single());
    }

    @Override
    public void emit(Book book) {
        final FluxSink<Book> sink = processor.sink();
        sink.next(book);
    }
}
