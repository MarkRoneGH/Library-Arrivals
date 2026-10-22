package proj.markrone.library.server.exceptions;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class ResponseException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(ResponseException.class);

    /**
     * Создает новое исключение с сообщением об ошибке.
     * Сообщение логируется на уровне ERROR.
     *
     * @param message сообщение об ошибке (будет отправлено клиенту)
     */
    public ResponseException(String message) {
        super(message);
        logger.error("Создано ResponseException: {}", message);
    }

    /**
     * Создает новое исключение с сообщением и причиной.
     * Сообщение и причина логируются на уровне ERROR.
     *
     * @param message сообщение об ошибке (будет отправлено клиенту)
     * @param cause исходное исключение
     */
    public ResponseException(String message, Throwable cause) {
        super(message, cause);
        logger.error("Создано ResponseException: {}", message, cause);
    }

}