package proj.markrone.library.client.gui.services;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.model.entities.Book;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.api.serializer.Serializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookService {
    private static final Logger logger = LogManager.getLogger(BookService.class);
    private final ServerClient serverClient = ServerClient.getInstance();

    public Response getAll() {
        try {
            return serverClient.sendRequest(new Request(Operation.GET_ALL_BOOKS));
        } catch (Exception e) {
            logger.error("Ошибка получения списка книг: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response addBook(Book book) {
        try {
            if (book == null) throw new IllegalArgumentException("Книга не может быть null");
            String json = Serializer.toJson(book);
            return serverClient.sendRequest(new Request(Operation.CREATE_BOOK, json));
        } catch (Exception e) {
            logger.error("Ошибка добавления книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response updateBook(Book book) {
        try {
            if (book == null) throw new IllegalArgumentException("Книга не может быть null");
            String json = Serializer.toJson(book);
            return serverClient.sendRequest(new Request(Operation.UPDATE_BOOK, json));
        } catch (Exception e) {
            logger.error("Ошибка обновления книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response deleteBook(Book book) {
        try {
            if (book == null || book.getId() == null) throw new IllegalArgumentException("Книга или ID не могут быть null");
            String json = Serializer.toJson(book.getId());
            return serverClient.sendRequest(new Request(Operation.DELETE_BOOK, json));
        } catch (Exception e) {
            logger.error("Ошибка удаления книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}
