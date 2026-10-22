package proj.markrone.library.api.network;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Модель ответа, представляющая собой сущность с результатом операции, сообщением и данными.
 */
@Data
@AllArgsConstructor
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String data;
}
