package com.example.booksservice.dto;

import com.example.booksservice.entity.Genre;
import com.example.booksservice.entity.Status;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private String title;
    private String author;
    private String ISBN;
    private String description;
    private Set<Genre> genre;
    private Set<Status> status;
}
