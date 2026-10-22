package proj.markrone.library.server.model.entities;

import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "Issues")
@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class Issue extends AbstractEntity {

    @Expose
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @Expose
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reader_id", nullable = false)
    private User reader;

    @Expose
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Expose
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Expose
    @Column(name = "returned_at")
    private LocalDateTime returnedAt;
}
