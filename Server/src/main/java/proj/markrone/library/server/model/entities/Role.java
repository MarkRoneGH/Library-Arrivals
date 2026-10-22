package proj.markrone.library.server.model.entities;

import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "Roles")
@Getter
@Setter
@ToString(callSuper = true)
public class Role extends AbstractEntity {

    @Expose
    @Column(unique = true, nullable = false)
    private String name;

    public Role() {}
}
