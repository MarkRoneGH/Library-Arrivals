package proj.markrone.library.server.interfaces;

import java.util.List;

/**
 * Базовый интерфейс для DAO (Data Access Object) операций.
 *
 * @param <T> тип сущности, с которой работает DAO
 */
public interface DAO<T> {
    void save(T obj);
    void update(T obj);
    void delete(T obj);
    T findById(int id);
    List<T> findAll();
}
