package proj.markrone.library.server.services;

import proj.markrone.library.server.interfaces.Service;
import proj.markrone.library.server.model.entities.AbstractEntity;
import proj.markrone.library.server.repositories.AbstractDAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Абстрактный сервис с базовыми CRUD-операциями.
 *
 * @param <T> тип сущности
 */
public abstract class AbstractService<T extends AbstractEntity> implements Service<T> {
    protected final Logger logger = LogManager.getLogger(getClass());

    protected abstract AbstractDAO<T> getDao();

    @Override
    public T findEntity(int id) {
        return getDao().findById(id);
    }

    @Override
    public void saveEntity(T entity) {
        getDao().save(entity);
    }

    @Override
    public void deleteEntity(T entity) {
        getDao().delete(entity);
    }

    @Override
    public void updateEntity(T entity) {
        getDao().update(entity);
    }

    @Override
    public List<T> findAllEntities() {
        return getDao().findAll();
    }
}
