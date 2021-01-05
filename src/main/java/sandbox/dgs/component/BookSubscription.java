package sandbox.dgs.component;

import java.time.Duration;

import org.reactivestreams.Publisher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import sandbox.dgs.data.DataProvider;
import sandbox.dgs.model.Book;
import sandbox.dgs.processor.IBookProcessor;

@AllArgsConstructor
@DgsComponent
public class BookSubscription {
    private final DataProvider dataProvider;
    private final IBookProcessor bookProcessor;

    @DgsData(parentType = "Subscription", field = "subscribeBooks")
    public Publisher<Book> subscribeBooks() {
        return bookProcessor.publish();
    }
}
