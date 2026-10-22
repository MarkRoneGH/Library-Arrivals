package proj.markrone.library.server.services;

import proj.markrone.library.server.model.entities.Role;
import proj.markrone.library.server.repositories.AbstractDAO;
import proj.markrone.library.server.repositories.RoleDAO;

public class RoleService extends AbstractService<Role> {
    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected AbstractDAO<Role> getDao() {
        return roleDAO;
    }

    public Role findRoleByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя роли не может быть пустым или null");
        }
        return roleDAO.findRoleByName(name);
    }
}
