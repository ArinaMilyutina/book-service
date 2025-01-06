package com.example.booksservice.service;

import com.example.booksservice.dto.BookRequest;
import com.example.booksservice.dto.BookResponse;
import com.example.booksservice.dto.ListBookResponse;
import com.example.booksservice.entity.Book;
import com.example.booksservice.exception.BookAlreadyExistsException;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.mapper.BookMapper;
import com.example.booksservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    private final static String BOOK_ALREADY_EXISTS = "The book with this isbn already exists!!!";
    private final static String BOOKS_NOT_FOUND = "Book not found.";
    private final static String BOOK_BY_ID = "A book with this id not found.";

    private final static String BOOK_BY_ISBN = "A book with isbn not found.";
    private final static String DELETE_BOOK_BY_ID = "The book has been deleted.";

    @Transactional
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = BookMapper.INSTANCE.bookDtoToBook(bookRequest);
        Optional<Book> existingBook = bookRepository.findByISBN(book.getISBN());
        if (existingBook.isPresent()) {
            throw new BookAlreadyExistsException(BOOK_ALREADY_EXISTS);
        }
        bookRepository.save(book);

        return createdBookResponse(book);
    }


    public ListBookResponse findAll() throws BookNotFoundException {
        List<Book> bookList = bookRepository.findAll();
        if (bookList.isEmpty()) {
            throw new BookNotFoundException(BOOKS_NOT_FOUND);
        }
        List<BookResponse> bookResponses = bookList.stream()
                .map(this::createdBookResponse)
                .collect(Collectors.toList());
        return ListBookResponse.builder()
                .responseList(bookResponses)
                .build();
    }


    public Optional<BookResponse> findById(Long id) throws BookNotFoundException {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            throw new BookNotFoundException(BOOK_BY_ID);
        }
        Book book = optionalBook.get();
        return Optional.ofNullable(createdBookResponse(book));
    }

    public Optional<BookResponse> findByISBN(String ISBN) throws BookNotFoundException {
        Optional<Book> optionalBook = bookRepository.findByISBN(ISBN);
        if (optionalBook.isEmpty()) {
            throw new BookNotFoundException(BOOK_BY_ISBN);
        }
        Book book = optionalBook.get();
        return Optional.ofNullable(createdBookResponse(book));
    }

    @Transactional
    public String deleteBookByISBN(String ISBN) throws BookNotFoundException {
        Optional<Book> book = bookRepository.findByISBN(ISBN);
        if (book.isEmpty()) {
            throw new BookNotFoundException(BOOKS_NOT_FOUND);
        }
        bookRepository.deleteByISBN(ISBN);
        return DELETE_BOOK_BY_ID;
    }

    public BookResponse updateBookByISBN(String ISBN, BookRequest bookRequest) throws BookNotFoundException {
        Optional<Book> optionalBook = bookRepository.findByISBN(ISBN);
        if (optionalBook.isEmpty()) {
            throw new BookNotFoundException(BOOK_BY_ISBN);
        }
        Book book = BookMapper.INSTANCE.bookDtoToBook(bookRequest);
        book.setId(optionalBook.get().getId());
        Optional<Book> existingBook = bookRepository.findByISBN(book.getISBN());
        if (existingBook.isPresent()) {
            throw new BookAlreadyExistsException(BOOK_ALREADY_EXISTS);
        }
        Book updatedBook = bookRepository.save(book);
        return createdBookResponse(updatedBook);
    }

    private BookResponse createdBookResponse(Book book) {
        return BookResponse.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .ISBN(book.getISBN())
                .genre(book.getGenre())
                .status(book.getStatus())
                .build();
    }

}


