package proj.markrone.library.server.repositories;

import proj.markrone.library.server.model.entities.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;

import java.util.List;


public class UserDAO extends AbstractDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);
            root.fetch("role", JoinType.LEFT);
            root.fetch("person", JoinType.LEFT);
            cq.select(root).where(cb.equal(root.get("username"), login));
            return session.createQuery(cq).uniqueResult();
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по логину '{}'", login, e);
            throw e;
        }
    }

    public List<User> findByRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Имя роли не может быть пустым");
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM User u WHERE u.role.name = :roleName", User.class)
                    .setParameter("roleName", roleName)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Ошибка получения пользователей по роли '{}'", roleName, e);
            throw e;
        }
    }
}
