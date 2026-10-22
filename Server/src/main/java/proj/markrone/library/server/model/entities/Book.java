package proj.markrone.library.server.model.entities;

import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "Books")
@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class Book extends AbstractEntity {

    @Expose
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Expose
    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Expose
    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @Expose
    @Column(name = "year_published")
    private Integer yearPublished;
}
