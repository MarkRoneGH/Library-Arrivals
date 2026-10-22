package proj.markrone.library.api.model.entities;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * Модель пользователя системы.
 */
@Data
public class User {
    @Expose
    private Integer id;
    @Expose
    private String username;
    @Expose
    private String passwordHash;
    @Expose
    private Role role;
    @Expose
    private Person person;
}
