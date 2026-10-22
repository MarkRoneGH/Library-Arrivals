package proj.markrone.library.api.serializer;

import proj.markrone.library.client.gui.utils.AlertUtil;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.List;


public class Deserializer {
    private static final Logger logger = LogManager.getLogger(Deserializer.class);

    /**
     * Извлекает данные из JSON строки в указанный тип.
     *
     * @param data строка данных в формате JSON
     * @param type тип, в который необходимо десериализовать данные
     * @param <T> обобщенный тип
     * @return десериализованные данные или null в случае ошибки
     */
    public <T> T extractData(String data, Type type) {
        try {
            return Serializer.buildGson().fromJson(data, type);
        } catch (JsonSyntaxException e) {
            logger.error("Ошибка десериализации: {}", e.getMessage());
            AlertUtil.error("Ошибка десериализации", "Не удалось десериализовать данные.");
            return null;
        }
    }

    /**
     * Извлекает список данных из JSON строки.
     *
     * @param data строка данных в формате JSON
     * @param classOfT класс типа элементов списка
     * @param <T> обобщенный тип
     * @return список десериализованных данных или null в случае ошибки
     */
    public <T> List<T> extractListData(String data, Class<T> classOfT) {
        Type listType = TypeToken.getParameterized(List.class, classOfT).getType();
        return extractData(data, listType);
    }
}
