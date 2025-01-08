package com.example.booksservice.dto;

import com.example.booksservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookInfoRequest {
    private Long bookId;
    private String title;
    private Set<Status> status;
}
