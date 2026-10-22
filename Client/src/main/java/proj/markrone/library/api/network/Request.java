package proj.markrone.library.api.network;

import proj.markrone.library.api.enums.Operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * Модель запроса, представляющая собой сущность с операцией и данными.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private Operation operation;
    private String data;
}
