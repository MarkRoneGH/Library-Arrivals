package proj.markrone.library.server.validators;

import proj.markrone.library.server.interfaces.Validatable;
import proj.markrone.library.server.model.entities.Book;

import java.time.Year;


public class BookValidator implements Validatable<Book> {

    @Override
    public boolean isValid(Book book) {
        if (book == null) return false;
        if (book.getTitle() == null || book.getTitle().isBlank()) return false;
        if (book.getAuthor() == null || book.getAuthor().isBlank()) return false;
        if (book.getYearPublished() != null) {
            if (book.getYearPublished() <= 0 || book.getYearPublished() > Year.now().getValue()) return false;
        }
        return true;
    }
}
