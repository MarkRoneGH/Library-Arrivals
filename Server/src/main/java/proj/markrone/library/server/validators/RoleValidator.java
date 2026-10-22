package proj.markrone.library.server.validators;

import proj.markrone.library.server.interfaces.Validatable;
import proj.markrone.library.server.model.entities.Role;


public class RoleValidator implements Validatable<Role> {

    /**
     * Проверяет, что объект Role не null и содержит непустое имя роли.
     *
     * @param role проверяемый объект Role
     * @return true, если объект корректен; false в противном случае
     */
    @Override
    public boolean isValid(Role role) {
        return role != null &&
                role.getName() != null &&
                !role.getName().isEmpty();
    }
}
