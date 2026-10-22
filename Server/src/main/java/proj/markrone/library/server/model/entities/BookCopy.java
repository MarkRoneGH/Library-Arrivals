package proj.markrone.library.server.model.entities;

import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "BookCopies")
@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class BookCopy extends AbstractEntity {

    @Expose
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Expose
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "arrival_id", nullable = false)
    private Arrival arrival;

    @Expose
    @Column(name = "inventory_number", nullable = false, unique = true, length = 50)
    private String inventoryNumber;

    @Expose
    @Column(name = "shelf_location", length = 100)
    private String shelfLocation;

    @Expose
    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
