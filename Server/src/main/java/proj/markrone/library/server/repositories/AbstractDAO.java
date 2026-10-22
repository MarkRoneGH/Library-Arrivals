package proj.markrone.library.server.repositories;

import proj.markrone.library.server.config.SessionConfig;
import proj.markrone.library.server.interfaces.DAO;
import proj.markrone.library.server.model.entities.AbstractEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Абстрактный DAO с generic CRUD-операциями через CriteriaBuilder.
 *
 * @param <T> тип сущности
 */
public abstract class AbstractDAO<T extends AbstractEntity> implements DAO<T> {
    protected final Logger logger = LogManager.getLogger(getClass());
    protected final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.sessionFactory = SessionConfig.getInstance().getSessionFactory();
    }

    @Override
    public void save(T obj) {
        executeTransaction(session -> session.persist(obj));
    }

    @Override
    public void update(T obj) {
        executeTransaction(session -> session.merge(obj));
    }

    @Override
    public void delete(T obj) {
        executeTransaction(session -> {
            T managed = session.contains(obj) ? obj : session.merge(obj);
            session.remove(managed);
        });
    }

    @Override
    public T findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entityClass, id);
        } catch (Exception e) {
            logger.error("Ошибка поиска {} по ID {}", entityClass.getSimpleName(), id, e);
            return null;
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            cq.select(cq.from(entityClass));
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения списка {}", entityClass.getSimpleName(), e);
            return List.of();
        }
    }

    protected void executeTransaction(SessionAction action) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            action.execute(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка транзакции", e);
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    protected interface SessionAction {
        void execute(Session session);
    }
}
