package proj.markrone.library.api.exceptions;

/**
 * Исключение, возникающее при отсутствии соединения.
 */
public class NoConnectionException extends RuntimeException {

    /**
     * Конструктор исключения с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public NoConnectionException(String message) {
        super(message);
    }
}
