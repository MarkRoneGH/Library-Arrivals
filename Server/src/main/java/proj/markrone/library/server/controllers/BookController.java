package proj.markrone.library.server.controllers;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.Book;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.serializer.Deserializer;
import proj.markrone.library.server.serializer.Serializer;
import proj.markrone.library.server.services.BookService;
import proj.markrone.library.server.validators.BookValidator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class BookController {
    private static final Logger logger = LogManager.getLogger(BookController.class);
    private final BookService bookService = new BookService();
    private final BookValidator bookValidator = new BookValidator();

    public Response createBook(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Book book)) {
                return new Response(false, "Некорректные данные книги", null);
            }
            if (!bookValidator.isValid(book)) {
                return new Response(false, "Данные книги не прошли валидацию (название, автор, год)", null);
            }
            Book created = bookService.create(book);
            return new Response(true, "Книга добавлена в каталог", Serializer.toJson(created));
        } catch (ResponseException | IllegalArgumentException e) {
            logger.warn("Ошибка создания книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response updateBook(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Book book)) {
                return new Response(false, "Некорректные данные книги", null);
            }
            if (!bookValidator.isValid(book)) {
                return new Response(false, "Данные книги не прошли валидацию (название, автор, год)", null);
            }
            bookService.updateEntity(book);
            return new Response(true, "Книга обновлена", null);
        } catch (ResponseException | IllegalArgumentException e) {
            logger.warn("Ошибка обновления книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response deleteBook(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Number)) {
                return new Response(false, "Некорректный ID книги", null);
            }
            int id = ((Number) data).intValue();
            Book book = bookService.findEntity(id);
            if (book == null) return new Response(false, "Книга не найдена", null);
            bookService.deleteEntity(book);
            return new Response(true, "Книга удалена", null);
        } catch (ResponseException | IllegalArgumentException e) {
            logger.warn("Ошибка удаления книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response getAllBooks() {
        try {
            List<Book> list = bookService.findAllEntities();
            return new Response(true, "Список книг получен", Serializer.toJson(list));
        } catch (Exception e) {
            logger.error("Ошибка получения списка книг", e);
            return new Response(false, "Ошибка при получении списка книг", null);
        }
    }

    public Response searchBooks(Request request) {
        try {
            String query = new Deserializer().extractData(request.getData(), String.class);
            List<Book> results = bookService.searchBooks(query);
            return new Response(true, "Результаты поиска", Serializer.toJson(results));
        } catch (Exception e) {
            logger.warn("Ошибка поиска книг: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}
