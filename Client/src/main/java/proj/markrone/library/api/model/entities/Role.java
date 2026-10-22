package proj.markrone.library.api.model.entities;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель роли пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Expose
    private Integer id;
    @Expose
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
