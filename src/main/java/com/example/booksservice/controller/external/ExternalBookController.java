package com.example.booksservice.controller.external;

import com.example.booksservice.dto.BookInfoRequest;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/book")
public class ExternalBookController {
    @Autowired
    private BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<BookInfoRequest> takeTheBook(@PathVariable Long id) throws BookNotFoundException {
        return ResponseEntity.ok(bookService.takeTheBook(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{id}")
    public ResponseEntity<Void> updateBookStatus(@PathVariable Long id) {
        bookService.updateBookStatus(id);
        return ResponseEntity.noContent().build();
    }
}
