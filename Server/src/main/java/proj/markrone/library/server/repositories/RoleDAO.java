package proj.markrone.library.server.repositories;

import proj.markrone.library.server.model.entities.Role;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;


public class RoleDAO extends AbstractDAO<Role> {

    public RoleDAO() {
        super(Role.class);
    }

    public Role findRoleByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя роли не может быть пустым");
        }
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Role> cq = cb.createQuery(Role.class);
            Root<Role> root = cq.from(Role.class);
            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).uniqueResult();
        } catch (Exception e) {
            logger.error("Ошибка при поиске роли по имени '{}'", name, e);
            throw e;
        }
    }
}
