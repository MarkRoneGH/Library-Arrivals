package proj.markrone.library.server.serializer;

import proj.markrone.library.api.network.Request;
import proj.markrone.library.server.model.entities.*;
import proj.markrone.library.server.utils.Pair;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class Deserializer {

    public <T> T extractData(String json, Class<T> clazz) {
        try {
            return Serializer.buildGson().fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Некорректный JSON-формат", e);
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public Object extractData(Request request) {
        Gson gson = Serializer.buildGson();
        try {
            return switch (request.getOperation()) {
                case LOGIN, CREATE_USER, REGISTER -> gson.fromJson(request.getData(), User.class);
                case DELETE_USER -> gson.fromJson(request.getData(), String.class);
                case UPDATE_USER -> gson.fromJson(request.getData(), new TypeToken<Pair<User, User>>() {}.getType());

                case CREATE_BOOK, UPDATE_BOOK -> gson.fromJson(request.getData(), Book.class);
                case DELETE_BOOK -> gson.fromJson(request.getData(), Integer.class);

                case CREATE_ARRIVAL, UPDATE_ARRIVAL -> gson.fromJson(request.getData(), Arrival.class);
                case DELETE_ARRIVAL -> gson.fromJson(request.getData(), Integer.class);

                case REGISTER_COPY -> gson.fromJson(request.getData(), BookCopy.class);
                case DELETE_COPY, INCLUDE_IN_FUND, WRITE_OFF_COPY -> gson.fromJson(request.getData(), Integer.class);
                case RELOCATE_COPY -> gson.fromJson(request.getData(), new TypeToken<Object[]>() {}.getType());

                case ISSUE_BOOK -> gson.fromJson(request.getData(), new TypeToken<Object[]>() {}.getType());
                case RETURN_BOOK, GET_ISSUES_BY_READER -> gson.fromJson(request.getData(), Integer.class);

                default -> null;
            };
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Некорректный JSON-формат", e);
        }
    }
}
