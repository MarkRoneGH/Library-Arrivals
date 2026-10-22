package proj.markrone.library.server.utils;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс-пара для хранения двух связанных объектов.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
@Data
@AllArgsConstructor
public class Pair<K, V> {
    @Expose
    private K key;
    @Expose
    private V value;
}
