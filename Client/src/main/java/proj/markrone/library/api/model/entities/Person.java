package proj.markrone.library.api.model.entities;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * Модель физического лица.
 */
@Data
public class Person {
    @Expose
    private Integer id;
    @Expose
    private String firstName;
    @Expose
    private String patronymic;
    @Expose
    private String lastName;
}
