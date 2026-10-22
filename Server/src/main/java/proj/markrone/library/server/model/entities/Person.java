package proj.markrone.library.server.model.entities;

import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "Persons")
@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class Person extends AbstractEntity {

    @Expose
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Expose
    @Column(name = "patronymic", length = 50)
    private String patronymic;

    @Expose
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
}
