package sandbox.dgs.data;

import java.util.List;

import sandbox.dgs.model.Book;

public interface DataProvider {
    List<Book> books();

    Book bookById(String bookId);
}
