package com.example.booksservice.controller;

import com.example.booksservice.dto.BookDto;
import com.example.booksservice.entity.Book;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.feignclient.UserClient;
import com.example.booksservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookDto bookDto) {
        Long currentUserId = userClient.getCurrentUserId();
        bookDto.setUserId(currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookDto));
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> findAll() throws BookNotFoundException {
        return ResponseEntity.of(Optional.of(bookService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id) throws BookNotFoundException {
        Optional<Book> book = bookService.findById(id);
        return book
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ISBN/{ISBN}")
    public ResponseEntity<Book> findByISBN(@PathVariable String ISBN) throws BookNotFoundException {
        Optional<Book> book = bookService.findByISBN(ISBN);
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
    public ResponseEntity<Book> updateByISBN(@PathVariable String ISBN, @Valid @RequestBody BookDto bookDto) throws BookNotFoundException {
        Long currentUserId = userClient.getCurrentUserId();
        bookDto.setUserId(currentUserId);
        Book book = bookService.updateBookByISBN(ISBN, bookDto);
        return ResponseEntity.ok(book);

    }

}


