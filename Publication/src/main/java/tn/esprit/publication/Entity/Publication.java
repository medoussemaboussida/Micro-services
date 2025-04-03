package tn.esprit.publication.Entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Publication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    private String author;

    // Constructeurs
    public Publication() {
        super();
    }
    public Publication(String title) {
        super();
        this.title = title;
    }

    public Publication(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.creationDate = new Date();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}