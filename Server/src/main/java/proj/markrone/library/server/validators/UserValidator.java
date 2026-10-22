package proj.markrone.library.server.validators;

import proj.markrone.library.server.interfaces.Validatable;
import proj.markrone.library.server.model.entities.User;


public class UserValidator implements Validatable<User> {

    /**
     * Полная проверка пользователя, включая вложенные объекты Person и Role.
     *
     * @param user объект пользователя для проверки
     * @return true, если все данные корректны, иначе false
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isFullValid(User user) {
        return isValid(user) &&
                new PersonValidator().isValid(user.getPerson()) &&
                new RoleValidator().isValid(user.getRole());
    }

    /**
     * Базовая проверка объекта User: проверка логина и хеша пароля.
     *
     * @param user объект пользователя для проверки
     * @return true, если логин и пароль заданы, иначе false
     */
    @Override
    public boolean isValid(User user) {
        return user != null &&
                user.getUsername() != null &&
                user.getUsername().trim().length() >= 4 &&
                user.getPasswordHash() != null &&
                user.getPasswordHash().length() >= 4;
    }
}
