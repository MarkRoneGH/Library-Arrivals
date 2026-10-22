package proj.markrone.library.server.controllers;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.BookCopy;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.serializer.Deserializer;
import proj.markrone.library.server.serializer.Serializer;
import proj.markrone.library.server.services.BookCopyService;
import proj.markrone.library.server.validators.BookCopyValidator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class BookCopyController {
    private static final Logger logger = LogManager.getLogger(BookCopyController.class);
    private final BookCopyService bookCopyService = new BookCopyService();
    private final BookCopyValidator bookCopyValidator = new BookCopyValidator();

    public Response registerCopy(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof BookCopy copy)) {
                return new Response(false, "Некорректные данные экземпляра", null);
            }
            if (!bookCopyValidator.isValid(copy)) {
                return new Response(false, "Данные экземпляра не прошли валидацию (инв. номер, статус)", null);
            }
            if (copy.getBook() == null || copy.getBook().getId() == null) {
                return new Response(false, "Укажите книгу (каталог)", null);
            }
            if (copy.getArrival() == null || copy.getArrival().getId() == null) {
                return new Response(false, "Укажите поступление", null);
            }
            bookCopyService.saveEntity(copy);
            return new Response(true, "Экземпляр зарегистрирован (присвоен инв. номер, полка)", Serializer.toJson(copy));
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка регистрации экземпляра: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response deleteCopy(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Number)) {
                return new Response(false, "Некорректный ID экземпляра", null);
            }
            int copyId = ((Number) data).intValue();
            BookCopy copy = bookCopyService.findEntity(copyId);
            if (copy == null) return new Response(false, "Экземпляр не найден", null);
            if ("ISSUED".equals(copy.getStatus())) {
                return new Response(false, "Нельзя удалить выданный экземпляр. Сначала примите книгу.", null);
            }
            bookCopyService.deleteEntity(copy);
            return new Response(true, "Экземпляр удалён", null);
        } catch (ResponseException e) {
            logger.warn("Ошибка удаления экземпляра: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response includeInFund(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Number)) {
                return new Response(false, "Некорректный ID экземпляра", null);
            }
            int copyId = ((Number) data).intValue();
            bookCopyService.includeInFund(copyId);
            return new Response(true, "Экземпляр включён в фонд (доступен для выдачи)", null);
        } catch (ResponseException e) {
            logger.warn("Ошибка включения в фонд: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response writeOffCopy(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Number)) {
                return new Response(false, "Некорректный ID экземпляра", null);
            }
            int copyId = ((Number) data).intValue();
            bookCopyService.writeOff(copyId);
            return new Response(true, "Экземпляр списан", null);
        } catch (ResponseException e) {
            logger.warn("Ошибка списания: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response relocateCopy(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Object[] arr) || arr.length != 2
                    || !(arr[0] instanceof Number)
                    || !(arr[1] instanceof String)) {
                return new Response(false, "Некорректные данные (ID экземпляра, новая полка)", null);
            }
            int copyId = ((Number) arr[0]).intValue();
            bookCopyService.relocate(copyId, (String) arr[1]);
            return new Response(true, "Экземпляр перемещён", null);
        } catch (ResponseException e) {
            logger.warn("Ошибка перемещения: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response getAllBookCopies() {
        try {
            List<BookCopy> list = bookCopyService.findAllEntities();
            return new Response(true, "Список экземпляров получен", Serializer.toJson(list));
        } catch (Exception e) {
            logger.error("Ошибка получения списка экземпляров", e);
            return new Response(false, "Ошибка при получении списка", null);
        }
    }

    public Response getAvailableCopies() {
        try {
            List<BookCopy> list = bookCopyService.findAvailable();
            return new Response(true, "Доступные для выдачи экземпляры получены", Serializer.toJson(list));
        } catch (Exception e) {
            logger.error("Ошибка получения доступных экземпляров", e);
            return new Response(false, "Ошибка при получении списка", null);
        }
    }

    public Response getCopiesByStatus(Request request) {
        try {
            String status = new Deserializer().extractData(request.getData(), String.class);
            var copies = bookCopyService.findByStatus(status);
            return new Response(true, "Экземпляры по статусу получены", Serializer.toJson(copies));
        } catch (Exception e) {
            logger.warn("Ошибка получения экземпляров по статусу: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}
