package proj.markrone.library.client.gui.services;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoleService {
    private static final Logger logger = LogManager.getLogger(RoleService.class);
    private final ServerClient serverClient;


    public RoleService() {
        this.serverClient = ServerClient.getInstance();
        logger.info("Инициализирован RoleService с клиентом сервера");
    }

    /**
     * Получает список всех ролей.
     *
     * @return ответ сервера со списком ролей
     */
    public Response getAll() {
        try {
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_ROLES));
            logger.info("Запрошен список всех ролей");
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка ролей: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}