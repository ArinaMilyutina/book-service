package com.example.booksservice.controller;

import com.example.booksservice.dto.BookRequest;
import com.example.booksservice.dto.BookResponse;
import com.example.booksservice.dto.ListBookResponse;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.feignclient.UserClient;
import com.example.booksservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private UserClient userClient;

    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        Long currentUserId = userClient.getCurrentUserId();
        bookRequest.setUserId(currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookRequest));
    }

    @GetMapping("/books")
    public ResponseEntity<ListBookResponse> findAll() throws BookNotFoundException {
        ListBookResponse response = bookService.findAll();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable Long id) throws BookNotFoundException {
        Optional<BookResponse> book = bookService.findById(id);
        return book
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ISBN/{ISBN}")
    public ResponseEntity<BookResponse> findByISBN(@PathVariable String ISBN) throws BookNotFoundException {
        Optional<BookResponse> book = bookService.findByISBN(ISBN);
        return book.map(ResponseEntity::ok)
                .orElseThrow(NoSuchElementException::new);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/{ISBN}")
    public ResponseEntity<String> deleteByISBN(@PathVariable String ISBN) throws BookNotFoundException {
        return new ResponseEntity<>(bookService.deleteBookByISBN(ISBN), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/{ISBN}")
    public ResponseEntity<BookResponse> updateByISBN(@PathVariable String ISBN, @Valid @RequestBody BookRequest bookRequest) throws BookNotFoundException {
        Long currentUserId = userClient.getCurrentUserId();
        bookRequest.setUserId(currentUserId);
        return ResponseEntity.ok(bookService.updateBookByISBN(ISBN, bookRequest));
    }

}


