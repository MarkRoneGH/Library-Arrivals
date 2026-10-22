package proj.markrone.library.server.repositories;

import proj.markrone.library.server.model.entities.Issue;

import org.hibernate.Session;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


public class IssueDAO extends AbstractDAO<Issue> {

    public IssueDAO() {
        super(Issue.class);
    }

    @Override
    public List<Issue> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Issue ORDER BY issuedAt DESC", Issue.class).getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения списка выдач", e);
            return Collections.emptyList();
        }
    }

    public List<Issue> findByReaderId(int readerId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Issue i WHERE i.reader.id = :rid ORDER BY i.issuedAt DESC", Issue.class)
                    .setParameter("rid", readerId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения выдач читателя {}", readerId, e);
            return Collections.emptyList();
        }
    }

    public List<Issue> findOverdue(LocalDate today) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Issue i WHERE i.returnedAt IS NULL AND i.dueDate < :today", Issue.class)
                    .setParameter("today", today)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения просроченных выдач", e);
            return Collections.emptyList();
        }
    }

    public List<Issue> findActive() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Issue i WHERE i.returnedAt IS NULL ORDER BY i.issuedAt DESC", Issue.class)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения активных выдач", e);
            return Collections.emptyList();
        }
    }

    public List<Issue> findReturned() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Issue i WHERE i.returnedAt IS NOT NULL ORDER BY i.returnedAt DESC", Issue.class)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения возвращённых выдач", e);
            return Collections.emptyList();
        }
    }


    public long countByBookCopyId(int bookCopyId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session
                    .createQuery("SELECT COUNT(i) FROM Issue i WHERE i.bookCopy.id = :bcid", Long.class)
                    .setParameter("bcid", bookCopyId)
                    .getSingleResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Ошибка подсчёта выдач по экземпляру {}", bookCopyId, e);
            throw new RuntimeException("Не удалось проверить выдачи экземпляра", e);
        }
    }
}
