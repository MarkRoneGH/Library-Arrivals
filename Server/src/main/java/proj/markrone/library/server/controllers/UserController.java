package proj.markrone.library.server.controllers;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.User;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.serializer.Deserializer;
import proj.markrone.library.server.serializer.Serializer;
import proj.markrone.library.server.services.IssueService;
import proj.markrone.library.server.services.PersonService;
import proj.markrone.library.server.services.RoleService;
import proj.markrone.library.server.services.UserService;
import proj.markrone.library.server.model.entities.Issue;
import proj.markrone.library.server.utils.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;


public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;
    private final PersonService personService;
    private final RoleService roleService;
    private final IssueService issueService;

    /**
     * Конструктор с внедрением зависимостей сервисов.
     *
     * @param userService сервис работы с пользователями
     * @param personService сервис работы с персональными данными
     * @param roleService сервис работы с ролями
     */
    public UserController(UserService userService, PersonService personService, RoleService roleService) {
        this.userService = userService;
        this.personService = personService;
        this.roleService = roleService;
        this.issueService = new IssueService();
        logger.info("Инициализирован UserController с внешними сервисами");
    }


    public UserController() {
        this.userService = new UserService();
        this.personService = new PersonService();
        this.roleService = new RoleService();
        this.issueService = new IssueService();
        logger.info("Инициализирован UserController со стандартными сервисами");
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param request запрос с данными для входа
     * @return ответ с результатом аутентификации
     */
    public Response login(Request request) {
        try {
            User user = (User) new Deserializer().extractData(request);
            logger.info("Попытка входа пользователя: {}", user.getUsername());

            User existingUser = userService.login(user);
            String loggedInUser = Serializer.toJson(existingUser);

            logger.info("Успешный вход пользователя: {}", user.getUsername());
            return new Response(true, "Аутентификация успешна", loggedInUser);
        } catch (ClassCastException e) {
            logger.error("Некорректный формат данных пользователя", e);
            return new Response(false, "Некорректные данные для входа", null);
        } catch (ResponseException e) {
            logger.warn("Ошибка аутентификации: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при аутентификации", e);
            return new Response(false, "Внутренняя ошибка сервера", null);
        }
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param request запрос с данными нового пользователя
     * @return ответ с результатом регистрации
     */
    public Response register(Request request) {
        try {
            Object extractedData = new Deserializer().extractData(request);

            if (!(extractedData instanceof User user)) {
                logger.warn("Попытка регистрации с некорректными данными");
                return new Response(false, "Некорректные данные пользователя", null);
            }

            logger.info("Попытка регистрации пользователя: {}", user.getUsername());
            User registeredUser = userService.register(user, personService, roleService);
            String registeredUserJson = Serializer.toJson(registeredUser);

            logger.info("Успешная регистрация пользователя: {}", user.getUsername());
            return new Response(true, "Регистрация успешна", registeredUserJson);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка формата данных при регистрации", e);
            return new Response(false, "Ошибка формата данных", null);
        } catch (ResponseException e) {
            logger.error("Ошибка регистрации: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Получает список всех пользователей.
     *
     * @return ответ со списком пользователей
     */
    public Response getAllUsers() {
        try {
            logger.info("Запрос списка всех пользователей");
            var users = userService.findAllEntities();
            String usersJson = Serializer.toJson(users);
            logger.info("Получено {} пользователей", users.size());
            return new Response(true, "Список пользователей получен", usersJson);
        } catch (Exception e) {
            logger.error("Ошибка получения списка пользователей", e);
            return new Response(false, "Ошибка при получении списка пользователей", null);
        }
    }


    public Response getAllReaders() {
        try {
            logger.info("Запрос списка читателей");
            var readers = userService.findReaders();
            String readersJson = Serializer.toJson(readers);
            logger.info("Получено {} читателей", readers.size());
            return new Response(true, "Список читателей получен", readersJson);
        } catch (Exception e) {
            logger.error("Ошибка получения списка читателей", e);
            return new Response(false, "Ошибка при получении списка читателей", null);
        }
    }

    /**
     * Удаляет пользователя.
     *
     * @param request запрос с логином пользователя
     * @return результат операции
     */
    public Response deleteUser(Request request) {
        try {
            String login = (String) new Deserializer().extractData(request);
            logger.info("Попытка удаления пользователя: {}", login);

            User foundUser = userService.findByUsername(login);
            if (foundUser == null) {
                logger.warn("Пользователь для удаления не найден: {}", login);
                return new Response(false, "Пользователь не найден", null);
            }

            List<Issue> userIssues = issueService.findByReaderId(foundUser.getId());

            boolean hasActiveIssues = userIssues.stream()
                    .anyMatch(issue -> issue.getReturnedAt() == null);
            if (hasActiveIssues) {
                logger.warn("Нельзя удалить пользователя {} — есть активные выдачи", login);
                return new Response(false, "Нельзя удалить пользователя с активными выдачами", null);
            }

            for (Issue issue : userIssues) {
                issueService.deleteEntity(issue);
            }

            personService.deleteEntity(foundUser.getPerson());
            logger.info("Пользователь успешно удален: {}", login);
            return new Response(true, "Пользователь удален", null);
        } catch (ClassCastException e) {
            logger.error("Некорректный формат логина пользователя", e);
            return new Response(false, "Некорректный формат данных", null);
        } catch (ResponseException e) {
            logger.error("Ошибка удаления пользователя: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при удалении пользователя", e);
            return new Response(false, "Ошибка при удалении пользователя: " + e.getMessage(), null);
        }
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param request запрос с данными для обновления
     * @return результат операции
     */
    public Response updateEntity(Request request) {
        try {
            Object extractData = new Deserializer().extractData(request);

            if (!(extractData instanceof Pair<?,?> pair) ||
                    !(pair.getKey() instanceof User userToUpdate) ||
                    !(pair.getValue() instanceof User userThatOperate)) {
                logger.warn("Некорректный формат данных для обновления пользователя");
                return new Response(false, "Некорректные данные пользователя", null);
            }

            logger.info("Попытка обновления пользователя ID {} (инициатор: {})",
                    userToUpdate.getId(), userThatOperate.getId());

            User existingUserToUpdate = userService.findEntity(userToUpdate.getId());
            User existingUserThatOperate = userService.findEntity(userThatOperate.getId());

            if (existingUserToUpdate == null || existingUserThatOperate == null) {
                logger.warn("Один из пользователей не существует (обновляемый: {}, инициатор: {})",
                        userToUpdate.getId(), userThatOperate.getId());
                return new Response(false, "Один из пользователей не существует", null);
            }

            userService.updateEntity(userToUpdate, personService);

            if (Objects.equals(userToUpdate.getId(), userThatOperate.getId())) {
                logger.info("Пользователь обновил свои данные: {}", userToUpdate.getId());
                return new Response(true, "Данные пользователя обновлены. Требуется повторный вход.", null);
            } else {
                logger.info("Администратор обновил данные пользователя: {}", userToUpdate.getId());
                return new Response(true, "Данные пользователя обновлены", null);
            }
        } catch (ResponseException e) {
            logger.error("Ошибка обновления пользователя: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}