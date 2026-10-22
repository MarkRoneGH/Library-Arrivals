package proj.markrone.library.server.controllers;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.serializer.Serializer;
import proj.markrone.library.server.services.RoleService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RoleController {
    private static final Logger logger = LogManager.getLogger(RoleController.class);
    private final RoleService roleService;

    /**
     * Конструктор с внедрением зависимости сервиса.
     *
     * @param roleService сервис для работы с ролями
     */
    public RoleController(final RoleService roleService) {
        this.roleService = roleService;
        logger.info("Инициализирован RoleController с внешним сервисом");
    }


    public RoleController() {
        this.roleService = new RoleService();
        logger.info("Инициализирован RoleController со стандартным сервисом");
    }

    /**
     * Получает список всех ролей.
     *
     * @return ответ со списком ролей в формате JSON
     */
    public Response getAllRoles() {
        try {
            logger.info("Запрос всех ролей");
            var roles = roleService.findAllEntities();
            String rolesJson = Serializer.toJson(roles);
            logger.info("Успешно получено {} ролей", roles.size());
            return new Response(true, "Список ролей успешно получен", rolesJson);
        } catch (ResponseException e) {
            logger.error("Ошибка при получении списка ролей: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Неизвестная ошибка при получении списка ролей", e);
            return new Response(false, "Внутренняя ошибка сервера", null);
        }
    }
}