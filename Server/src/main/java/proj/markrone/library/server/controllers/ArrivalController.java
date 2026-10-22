package proj.markrone.library.server.controllers;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.Arrival;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.serializer.Deserializer;
import proj.markrone.library.server.serializer.Serializer;
import proj.markrone.library.server.services.ArrivalService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class ArrivalController {
    private static final Logger logger = LogManager.getLogger(ArrivalController.class);
    private final ArrivalService arrivalService = new ArrivalService();

    public Response createArrival(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Arrival arrival)) {
                return new Response(false, "Некорректные данные поступления", null);
            }
            Arrival created = arrivalService.create(arrival);
            return new Response(true, "Поступление зарегистрировано", Serializer.toJson(created));
        } catch (ResponseException | IllegalArgumentException e) {
            logger.warn("Ошибка создания поступления: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response updateArrival(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Arrival arrival)) {
                return new Response(false, "Некорректные данные поступления", null);
            }
            arrivalService.updateEntity(arrival);
            return new Response(true, "Поступление обновлено", null);
        } catch (ResponseException | IllegalArgumentException e) {
            logger.warn("Ошибка обновления поступления: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response deleteArrival(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Number)) {
                return new Response(false, "Некорректный ID поступления", null);
            }
            int id = ((Number) data).intValue();
            Arrival arrival = arrivalService.findEntity(id);
            if (arrival == null) return new Response(false, "Поступление не найдено", null);
            arrivalService.deleteEntity(arrival);
            return new Response(true, "Поступление удалено", null);
        } catch (ResponseException | IllegalArgumentException e) {
            logger.warn("Ошибка удаления поступления: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response getAllArrivals() {
        try {
            List<Arrival> list = arrivalService.findAllEntities();
            return new Response(true, "Список поступлений получен", Serializer.toJson(list));
        } catch (Exception e) {
            logger.error("Ошибка получения списка поступлений", e);
            return new Response(false, "Ошибка при получении списка поступлений", null);
        }
    }
}
