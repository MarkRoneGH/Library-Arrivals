package proj.markrone.library.server.repositories;

import proj.markrone.library.server.model.entities.BookCopy;

import org.hibernate.Session;

import java.util.Collections;
import java.util.List;

/**
 * DAO для работы с экземплярами книг (BookCopy).
 */
public class BookCopyDAO extends AbstractDAO<BookCopy> {

    public BookCopyDAO() {
        super(BookCopy.class);
    }

    public List<BookCopy> findAvailable() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM BookCopy bc WHERE bc.status = 'IN_FUND'", BookCopy.class)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения доступных экземпляров", e);
            return Collections.emptyList();
        }
    }

    public List<BookCopy> findByStatus(String status) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM BookCopy bc WHERE bc.status = :status", BookCopy.class)
                    .setParameter("status", status)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения экземпляров по статусу {}", status, e);
            return Collections.emptyList();
        }
    }
}
