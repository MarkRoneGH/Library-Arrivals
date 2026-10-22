package proj.markrone.library.api.model.entities;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Модель выдачи книги читателю.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Issue {
    @Expose
    private Integer id;
    @Expose
    private BookCopy bookCopy;
    @Expose
    private User reader;
    @Expose
    private LocalDateTime issuedAt;
    @Expose
    private LocalDate dueDate;
    @Expose
    private LocalDateTime returnedAt;
}
