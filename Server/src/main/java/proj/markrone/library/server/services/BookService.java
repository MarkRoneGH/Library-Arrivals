package proj.markrone.library.server.services;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.Book;
import proj.markrone.library.server.repositories.AbstractDAO;
import proj.markrone.library.server.repositories.BookDAO;

import java.util.List;

public class BookService extends AbstractService<Book> {
    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected AbstractDAO<Book> getDao() {
        return bookDAO;
    }

    @Override
    public void deleteEntity(Book entity) throws ResponseException {
        if (entity == null || entity.getId() == null)
            throw new IllegalArgumentException("Книга для удаления должна иметь ID");
        Book existing = bookDAO.findById(entity.getId());
        if (existing == null) throw new ResponseException("Книга не найдена.");
        bookDAO.delete(existing);
    }

    @Override
    public void updateEntity(Book entity) throws ResponseException {
        if (entity == null || entity.getId() == null)
            throw new IllegalArgumentException("Книга для обновления должна иметь ID");
        Book existing = bookDAO.findById(entity.getId());
        if (existing == null) throw new ResponseException("Книга не найдена.");
        bookDAO.update(entity);
    }

    public Book create(Book book) throws ResponseException {
        if (book == null || book.getTitle() == null || book.getTitle().isBlank())
            throw new IllegalArgumentException("Название книги не может быть пустым");
        bookDAO.save(book);
        return book;
    }

    public List<Book> searchBooks(String query) {
        return bookDAO.searchBooks(query);
    }
}
