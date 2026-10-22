package proj.markrone.library.api.model.entities;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель экземпляра книги (инв. номер, полка, статус).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {
    @Expose
    private Integer id;
    @Expose
    private Book book;
    @Expose
    private Arrival arrival;
    @Expose
    private String inventoryNumber;
    @Expose
    private String shelfLocation;
    @Expose
    private String status;
}
