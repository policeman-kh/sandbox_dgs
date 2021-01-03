package sandbox.dgs.fetcher;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.List;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;

import lombok.AllArgsConstructor;
import sandbox.dgs.data.DataProvider;
import sandbox.dgs.model.Book;

@AllArgsConstructor
@DgsComponent
public class BookFetcher {
    private final DataProvider dataProvider;

    @DgsData(parentType = "Query", field = "books")
    public List<Book> books(){
        return dataProvider.books();
    }

    @DgsData(parentType = "Query", field = "bookById")
    public List<Book> books(@InputArgument("id") String id){
        if(isNullOrEmpty(id)) {
            return dataProvider.books();
        }
        return List.of(dataProvider.bookById(id));
    }
}
