package sandbox.dgs.component;

import java.util.List;

import org.reactivestreams.Publisher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;

import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import sandbox.dgs.data.DataProvider;
import sandbox.dgs.model.Book;
import sandbox.dgs.processor.IBookProcessor;

@AllArgsConstructor
@DgsComponent
public class BookFetcher {
    private final DataProvider dataProvider;
    private final IBookProcessor bookProcessor;

    @DgsData(parentType = "Query", field = "books")
    public List<Book> books() {
        return dataProvider.books();
    }

    @DgsData(parentType = "Query", field = "bookById")
    public List<Book> books(@InputArgument("id") String id) {
        if (id == null || id.isEmpty()) {
            return dataProvider.books();
        }
        return List.of(dataProvider.bookById(id));
    }

    @DgsData(parentType = "Mutation", field = "registerBook")
    public Book registerBook(DataFetchingEnvironment dataFetchingEnvironment) {
        final String id = dataFetchingEnvironment.getArgument("id");
        final String name = dataFetchingEnvironment.getArgument("name");
        final int pageCount = dataFetchingEnvironment.getArgument("pageCount");

        final Book book = new Book(id, name, pageCount);
        dataProvider.books().add(book);
        // Emit an event for subscription.
        bookProcessor.emit(book);
        return book;
    }

    @DgsData(parentType = "Subscription", field = "subscribeBooks")
    public Publisher<Book> subscribeBooks() {
        return bookProcessor.publish();
    }
}
