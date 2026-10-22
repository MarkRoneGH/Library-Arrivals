package proj.markrone.library.server.validators;

import proj.markrone.library.server.model.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookValidatorTest {

    private BookValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BookValidator();
    }

    @Test
    void validBook_returnsTrue() {
        Book book = new Book();
        book.setTitle("Война и мир");
        book.setAuthor("Лев Толстой");
        book.setYearPublished(1869);

        assertTrue(validator.isValid(book));
    }

    @Test
    void bookWithBlankTitle_returnsFalse() {
        Book book = new Book();
        book.setTitle("   ");
        book.setAuthor("Лев Толстой");
        book.setYearPublished(1869);

        assertFalse(validator.isValid(book));
    }

    @Test
    void bookWithNullTitle_returnsFalse() {
        Book book = new Book();
        book.setTitle(null);
        book.setAuthor("Лев Толстой");
        book.setYearPublished(1869);

        assertFalse(validator.isValid(book));
    }

    @Test
    void bookWithBlankAuthor_returnsFalse() {
        Book book = new Book();
        book.setTitle("Война и мир");
        book.setAuthor("   ");
        book.setYearPublished(1869);

        assertFalse(validator.isValid(book));
    }
}
