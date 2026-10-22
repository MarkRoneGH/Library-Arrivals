package proj.markrone.library.api.model.entities;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель книги в каталоге библиотеки.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Expose
    private Integer id;
    @Expose
    private String title;
    @Expose
    private String author;
    @Expose
    private String isbn;
    @Expose
    private Integer yearPublished;
}
