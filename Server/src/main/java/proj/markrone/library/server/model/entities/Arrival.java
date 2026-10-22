package proj.markrone.library.server.model.entities;

import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;


@Entity
@Table(name = "Arrivals")
@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class Arrival extends AbstractEntity {

    @Expose
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Expose
    @Column(name = "notes", length = 500)
    private String notes;
}
