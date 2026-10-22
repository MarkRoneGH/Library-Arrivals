package proj.markrone.library.server.services;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.Person;
import proj.markrone.library.server.model.entities.Role;
import proj.markrone.library.server.model.entities.User;
import proj.markrone.library.server.repositories.AbstractDAO;
import proj.markrone.library.server.repositories.UserDAO;
import proj.markrone.library.server.utils.PasswordHasher;
import proj.markrone.library.server.validators.UserValidator;

import java.util.List;
import java.util.Objects;

public class UserService extends AbstractService<User> {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected AbstractDAO<User> getDao() {
        return userDAO;
    }

    public User login(User user) {
        if (!new UserValidator().isValid(user)) {
            throw new ResponseException("Ошибка входа: отсутствует логин или пароль");
        }

        User existingUser = userDAO.findByLogin(user.getUsername());

        if (existingUser != null
                && PasswordHasher.checkPassword(user.getPasswordHash(), existingUser.getPasswordHash())) {
            return existingUser;
        } else {
            throw new ResponseException("Ошибка входа: неверный логин или пароль");
        }
    }

    public User register(User user, PersonService personService, RoleService roleService) throws ResponseException {
        if (!new UserValidator().isFullValid(user)) {
            throw new ResponseException("Ошибка регистрации: неполные данные");
        }

        User existingUser = userDAO.findByLogin(user.getUsername());
        if (existingUser != null) {
            throw new ResponseException("Ошибка регистрации: пользователь с таким логином уже существует");
        }

        Person person = user.getPerson();
        personService.saveEntity(person);
        user.setPerson(person);

        Role role = roleService.findRoleByName(user.getRole().getName());
        if (role == null) {
            throw new ResponseException("Ошибка регистрации: роль не найдена");
        }
        user.setRole(role);

        user.setPasswordHash(PasswordHasher.hashPassword(user.getPasswordHash()));
        userDAO.save(user);
        return user;
    }

    @Override
    public void deleteEntity(User userToDelete) throws ResponseException {
        User existingUser = userDAO.findByLogin(userToDelete.getUsername());
        if (existingUser == null) {
            throw new ResponseException("Пользователь не найден.");
        }
        userDAO.delete(existingUser);
    }

    public void updateEntity(User newUser, PersonService personService) {
        User existingUser = userDAO.findById(newUser.getId());
        if (existingUser == null) {
            throw new ResponseException("Ошибка обновления: пользователь не существует");
        }

        User userWithSuchUsername = userDAO.findByLogin(newUser.getUsername());
        if (userWithSuchUsername != null && !Objects.equals(userWithSuchUsername.getId(), existingUser.getId())) {
            throw new ResponseException("Ошибка обновления: пользователь с таким логином уже существует");
        }

        if (!new UserValidator().isFullValid(newUser)) {
            throw new ResponseException("Ошибка обновления: данные пользователя некорректны");
        }

        if (newUser.getPasswordHash() != null && !newUser.getPasswordHash().startsWith("$2a$")) {
            newUser.setPasswordHash(PasswordHasher.hashPassword(newUser.getPasswordHash()));
        }

        personService.updateEntity(newUser.getPerson());
        userDAO.update(newUser);
    }

    public User findByUsername(String username) {
        return userDAO.findByLogin(username);
    }

    public List<User> findReaders() {
        return userDAO.findByRoleName("Reader");
    }
}
