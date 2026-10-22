package proj.markrone.library.api.model.entities;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Модель поступления партии книг в библиотеку.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Arrival {
    @Expose
    private Integer id;
    @Expose
    private LocalDate arrivalDate;
    @Expose
    private String notes;
}
