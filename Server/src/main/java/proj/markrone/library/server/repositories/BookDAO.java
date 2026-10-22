package proj.markrone.library.server.repositories;

import proj.markrone.library.server.model.entities.Book;

import org.hibernate.Session;

import java.util.List;

public class BookDAO extends AbstractDAO<Book> {

    public BookDAO() {
        super(Book.class);
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) return findAll();
        try (Session session = sessionFactory.openSession()) {
            String pattern = "%" + query.toLowerCase() + "%";
            return session.createQuery(
                    "FROM Book b WHERE LOWER(b.title) LIKE :q OR LOWER(b.author) LIKE :q OR LOWER(b.isbn) LIKE :q",
                    Book.class)
                    .setParameter("q", pattern)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка поиска книг по запросу '{}'", query, e);
            return List.of();
        }
    }
}
