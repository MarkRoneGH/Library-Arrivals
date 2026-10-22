package proj.markrone.library.server.network;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.controllers.*;
import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;


public class ClientThread implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientThread.class);

    private final Socket clientSocket;
    private final BookController bookController;
    private final ArrivalController arrivalController;
    private final BookCopyController bookCopyController;
    private final IssueController issueController;
    private final UserController userController;
    private final RoleController roleController;
    private final ExportController exportController;

    private User currentUser;

    private static final Set<Operation> PUBLIC_OPS = Set.of(
            Operation.LOGIN, Operation.REGISTER, Operation.DISCONNECT
    );

    private static final Set<Operation> READER_OPS = Set.of(
            Operation.GET_ISSUES_BY_READER, Operation.RETURN_BOOK,
            Operation.UPDATE_USER, Operation.GET_ALL_BOOKS, Operation.GET_ALL_ROLES
    );

    private static final Set<Operation> USER_MANAGEMENT_OPS = Set.of(
            Operation.GET_ALL_USERS, Operation.CREATE_USER, Operation.DELETE_USER
    );

    public ClientThread(Socket socket) {
        this.clientSocket = socket;
        bookController = new BookController();
        arrivalController = new ArrivalController();
        bookCopyController = new BookCopyController();
        issueController = new IssueController();
        userController = new UserController();
        roleController = new RoleController();
        exportController = new ExportController();
        logger.info("Инициализирован новый клиентский поток для сокета {}", socket);
    }

    @Override
    public void run() {
        try (
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            boolean keepRunning = true;

            while (keepRunning) {
                try {
                    Object obj = input.readObject();
                    if (!(obj instanceof Request request)) {
                        logger.warn("Получен некорректный объект вместо запроса от клиента");
                        Response errorResponse = new Response(false, "Получен некорректный объект запроса", null);
                        output.writeObject(errorResponse);
                        output.flush();
                        continue;
                    }

                    Response response = processRequest(request);
                    if (request.getOperation() == Operation.DISCONNECT) {
                        keepRunning = false;
                    }

                    output.writeObject(response);
                    output.flush();
                } catch (IOException e) {
                    logger.error("Ошибка соединения с клиентом: {}", e.getMessage());
                    keepRunning = false;
                } catch (ClassNotFoundException e) {
                    logger.error("Класс запроса не найден: {}", e.getMessage());
                    keepRunning = false;
                } catch (Exception e) {
                    logger.error("Необработанная ошибка при обработке запроса", e);
                    keepRunning = false;
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка инициализации потоков ввода-вывода клиента", e);
            throw new RuntimeException("Ошибка инициализации клиентского потока", e);
        } finally {
            closeConnection();
        }
    }

    private boolean checkAccess(Operation operation) {
        if (PUBLIC_OPS.contains(operation)) return true;
        if (currentUser == null) return false;

        String role = currentUser.getRole().getName();
        return switch (role) {
            case "Admin" -> true;
            case "Librarian" -> !USER_MANAGEMENT_OPS.contains(operation);
            case "Reader" -> READER_OPS.contains(operation) || PUBLIC_OPS.contains(operation);
            default -> false;
        };
    }

    private Response processRequest(Request request) {
        try {
            if (!checkAccess(request.getOperation())) {
                logger.warn("Отказ в доступе: пользователь {} (роль {}) → операция {}",
                        currentUser != null ? currentUser.getUsername() : "анонимный",
                        currentUser != null ? currentUser.getRole().getName() : "нет",
                        request.getOperation());
                return new Response(false, "Недостаточно прав", null);
            }

            Response response = executeOperation(request);

            if (request.getOperation() == Operation.LOGIN && response.isSuccess()) {
                try {
                    currentUser = new proj.markrone.library.server.serializer.Deserializer()
                            .extractData(response.getData(), User.class);
                    logger.info("Пользователь аутентифицирован: {} (роль {})",
                            currentUser.getUsername(), currentUser.getRole().getName());
                } catch (Exception e) {
                    logger.warn("Не удалось извлечь пользователя из ответа LOGIN", e);
                }
            }

            return response;
        } catch (ResponseException e) {
            logger.warn("Ошибка при выполнении операции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Внутренняя ошибка сервера при обработке запроса", e);
            return new Response(false, "Внутренняя ошибка сервера", null);
        }
    }

    private Response executeOperation(Request request) throws ResponseException {
        return switch (request.getOperation()) {
            case CREATE_BOOK -> bookController.createBook(request);
            case UPDATE_BOOK -> bookController.updateBook(request);
            case DELETE_BOOK -> bookController.deleteBook(request);
            case GET_ALL_BOOKS -> bookController.getAllBooks();

            case CREATE_ARRIVAL -> arrivalController.createArrival(request);
            case UPDATE_ARRIVAL -> arrivalController.updateArrival(request);
            case DELETE_ARRIVAL -> arrivalController.deleteArrival(request);
            case GET_ALL_ARRIVALS -> arrivalController.getAllArrivals();

            case REGISTER_COPY -> bookCopyController.registerCopy(request);
            case DELETE_COPY -> bookCopyController.deleteCopy(request);
            case INCLUDE_IN_FUND -> bookCopyController.includeInFund(request);
            case WRITE_OFF_COPY -> bookCopyController.writeOffCopy(request);
            case RELOCATE_COPY -> bookCopyController.relocateCopy(request);
            case GET_ALL_BOOK_COPIES -> bookCopyController.getAllBookCopies();
            case GET_AVAILABLE_COPIES -> bookCopyController.getAvailableCopies();

            case ISSUE_BOOK -> issueController.issueBook(request);
            case RETURN_BOOK -> issueController.returnBook(request);
            case GET_ISSUES_BY_READER -> issueController.getIssuesByReader(request);
            case GET_OVERDUE_ISSUES -> issueController.getOverdueIssues();
            case GET_ALL_ISSUES -> issueController.getAllIssues();

            case DELETE_USER -> userController.deleteUser(request);
            case UPDATE_USER -> userController.updateEntity(request);
            case CREATE_USER, REGISTER -> userController.register(request);
            case GET_ALL_USERS -> userController.getAllUsers();
            case GET_ALL_READERS -> userController.getAllReaders();

            case GET_ALL_ROLES -> roleController.getAllRoles();

            case LOGIN -> userController.login(request);

            case EXPORT_BOOKS_EXCEL -> exportController.exportBooksExcel();
            case EXPORT_BOOKS_PDF -> exportController.exportBooksPdf();
            case EXPORT_BOOKS_JSON -> exportController.exportBooksJson();

            case SEARCH_BOOKS -> bookController.searchBooks(request);
            case GET_COPIES_BY_STATUS -> bookCopyController.getCopiesByStatus(request);
            case GET_ACTIVE_ISSUES -> issueController.getActiveIssues();
            case GET_RETURNED_ISSUES -> issueController.getReturnedIssues();

            case DISCONNECT -> new Response(true, "Отключение выполнено успешно", null);
        };
    }

    private void closeConnection() {
        try {
            clientSocket.close();
            Server.decrementClientCount();
            logger.info("Клиентское соединение закрыто");
        } catch (IOException e) {
            logger.error("Ошибка при закрытии клиентского соединения", e);
        }
    }
}
