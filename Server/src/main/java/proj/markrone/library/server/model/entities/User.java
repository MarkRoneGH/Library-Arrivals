package proj.markrone.library.server.model.entities;

import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "Users")
@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class User extends AbstractEntity {

    @Expose
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Expose
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Expose
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Expose
    @OneToOne
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;
}
