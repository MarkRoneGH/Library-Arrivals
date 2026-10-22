package proj.markrone.library.api.network;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.exceptions.NoConnectionException;
import proj.markrone.library.api.model.entities.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;

/**
 * Класс для управления подключением клиента к серверу.
 * Реализует паттерн Singleton для обеспечения единственного экземпляра клиента.
 */
public class ServerClient {
    private static final Logger logger = LogManager.getLogger(ServerClient.class);
    private static ServerClient instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @Getter
    @Setter
    private static User currentUser;

    /**
     * Приватный конструктор для инициализации подключения к серверу.
     *
     * @throws NoConnectionException если не удалось подключиться к серверу
     */
    private ServerClient() throws NoConnectionException {
        connect(); // Если подключение провалится, выбросится исключение NoConnectionException
    }

    /**
     * Метод для получения единственного экземпляра класса ServerClient.
     *
     * @return единственный экземпляр ServerClient
     * @throws NoConnectionException если не удалось подключиться к серверу
     */
    public static synchronized ServerClient getInstance() throws NoConnectionException {
        if (instance == null) {
            try {
                instance = new ServerClient();
            } catch (Exception e) {
                logger.error("Ошибка при создании экземпляра ServerClient: {}", e.getMessage());
                throw new NoConnectionException("Не удалось создать экземпляр ServerClient");
            }
        }
        return instance;
    }

    /**
     * Метод для подключения к серверу.
     *
     * @throws NoConnectionException если не удалось подключиться к серверу
     */
    private void connect() throws NoConnectionException {
        ResourceBundle bundle = ResourceBundle.getBundle("server");

        String serverAddress = bundle.getString("SERVER_IP");
        int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));

        try {
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            logger.info("Подключено к серверу по адресу {}:{}", serverAddress, serverPort);
        } catch (IOException e) {
            throw new NoConnectionException("Не удалось подключиться к серверу по адресу " + serverAddress + ":" + serverPort);
        }
    }


    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }


    public synchronized boolean probeServerAlive() {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            return false;
        }
        int previousTimeout = 0;
        try {
            previousTimeout = socket.getSoTimeout();
            socket.setSoTimeout(2500);
            out.writeObject(new Request(Operation.GET_ALL_ROLES));
            out.flush();
            Object obj = in.readObject();
            return obj instanceof Response;
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("Проверка соединения: нет ответа сервера ({})", e.getMessage());
            return false;
        } finally {
            try {
                socket.setSoTimeout(previousTimeout);
            } catch (IOException ignored) {
            }
        }
    }


    public synchronized void disconnect() {
        try {
            if (out != null && socket != null && !socket.isClosed()) {
                try {
                    out.writeObject(new Request(Operation.DISCONNECT));
                    out.flush();
                } catch (IOException e) {
                    logger.warn("Не удалось отправить DISCONNECT: {}", e.getMessage());
                }
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.error("Ошибка при отключении от сервера: {}", e.getMessage());
        } finally {
            instance = null;
            currentUser = null;
            logger.info("Отключено от сервера.");
        }
    }

    /**
     * Отправляет запрос на сервер и возвращает ответ.
     *
     * @param request запрос для отправки
     * @return ответ от сервера
     */
    public synchronized Response sendRequest(Request request) {
        try {
            logger.info("Отправка запроса: {}", request.getOperation());
            out.writeObject(request);
            out.flush();

            return processResponse();
        } catch (IOException e) {
            logger.error("Ошибка при отправке запроса: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Обрабатывает ответ от сервера.
     *
     * @return ответ от сервера
     */
    private Response processResponse() {
        try {
            return (Response) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка при обработке ответа: {}", e.getMessage());
            return null;
        }
    }
}
