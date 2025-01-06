package com.example.booksservice.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books", schema = "book_service_schema")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String author;
    private String ISBN;
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "book_genres", schema = "book_service_schema",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @Enumerated(EnumType.STRING)
    private Set<Genre> genre;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_status", schema = "book_service_schema", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "status", nullable = false)
    private Set<Status> status;
    @Column(name = "admin_id")
    private Long userId;
}

