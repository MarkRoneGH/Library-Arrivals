package proj.markrone.library.client.gui.services;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.model.entities.BookCopy;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.api.serializer.Serializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookCopyService {
    private static final Logger logger = LogManager.getLogger(BookCopyService.class);
    private final ServerClient serverClient = ServerClient.getInstance();

    public Response getAllBookCopies() {
        try {
            return serverClient.sendRequest(new Request(Operation.GET_ALL_BOOK_COPIES));
        } catch (Exception e) {
            logger.error("Ошибка получения списка экземпляров: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response getAvailableCopies() {
        try {
            return serverClient.sendRequest(new Request(Operation.GET_AVAILABLE_COPIES));
        } catch (Exception e) {
            logger.error("Ошибка получения доступных экземпляров: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response registerCopy(BookCopy copy) {
        try {
            if (copy == null) throw new IllegalArgumentException("Экземпляр не может быть null");
            String json = Serializer.toJson(copy);
            return serverClient.sendRequest(new Request(Operation.REGISTER_COPY, json));
        } catch (Exception e) {
            logger.error("Ошибка регистрации экземпляра: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response deleteCopy(int copyId) {
        try {
            String json = Serializer.toJson(copyId);
            return serverClient.sendRequest(new Request(Operation.DELETE_COPY, json));
        } catch (Exception e) {
            logger.error("Ошибка удаления экземпляра: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response includeInFund(int copyId) {
        try {
            String json = Serializer.toJson(copyId);
            return serverClient.sendRequest(new Request(Operation.INCLUDE_IN_FUND, json));
        } catch (Exception e) {
            logger.error("Ошибка включения в фонд: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response writeOffCopy(int copyId) {
        try {
            String json = Serializer.toJson(copyId);
            return serverClient.sendRequest(new Request(Operation.WRITE_OFF_COPY, json));
        } catch (Exception e) {
            logger.error("Ошибка списания: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response relocateCopy(int copyId, String newShelf) {
        try {
            Object[] data = new Object[]{ copyId, newShelf };
            String json = Serializer.toJson(data);
            return serverClient.sendRequest(new Request(Operation.RELOCATE_COPY, json));
        } catch (Exception e) {
            logger.error("Ошибка перемещения: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}
