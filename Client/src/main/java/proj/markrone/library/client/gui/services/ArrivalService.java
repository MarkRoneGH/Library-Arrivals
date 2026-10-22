package proj.markrone.library.client.gui.services;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.model.entities.Arrival;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.api.serializer.Serializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArrivalService {
    private static final Logger logger = LogManager.getLogger(ArrivalService.class);
    private final ServerClient serverClient = ServerClient.getInstance();

    public Response getAll() {
        try {
            return serverClient.sendRequest(new Request(Operation.GET_ALL_ARRIVALS));
        } catch (Exception e) {
            logger.error("Ошибка получения списка поступлений: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response addArrival(Arrival arrival) {
        try {
            if (arrival == null) throw new IllegalArgumentException("Поступление не может быть null");
            String json = Serializer.toJson(arrival);
            return serverClient.sendRequest(new Request(Operation.CREATE_ARRIVAL, json));
        } catch (Exception e) {
            logger.error("Ошибка регистрации поступления: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response updateArrival(Arrival arrival) {
        try {
            if (arrival == null) throw new IllegalArgumentException("Поступление не может быть null");
            String json = Serializer.toJson(arrival);
            return serverClient.sendRequest(new Request(Operation.UPDATE_ARRIVAL, json));
        } catch (Exception e) {
            logger.error("Ошибка обновления поступления: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response deleteArrival(Arrival arrival) {
        try {
            if (arrival == null || arrival.getId() == null) throw new IllegalArgumentException("Поступление или ID не могут быть null");
            String json = Serializer.toJson(arrival.getId());
            return serverClient.sendRequest(new Request(Operation.DELETE_ARRIVAL, json));
        } catch (Exception e) {
            logger.error("Ошибка удаления поступления: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}
