package com.example.booksservice.service;

import com.example.booksservice.dto.BookDto;
import com.example.booksservice.entity.Book;
import com.example.booksservice.exception.BookAlreadyExistsException;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.mapper.BookMapper;
import com.example.booksservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Book createBook(BookDto bookDto) {
        Book book = BookMapper.INSTANCE.bookDtoToBook(bookDto);
        Optional<Book> existingBook = bookRepository.findByISBN(book.getISBN());
        if (existingBook.isPresent()) {
            throw new BookAlreadyExistsException(BOOK_ALREADY_EXISTS);
        }
        return bookRepository.save(book);
    }


    public List<Book> findAll() throws BookNotFoundException {
        List<Book> bookList = bookRepository.findAll();
        if (bookList.isEmpty()) {
            throw new BookNotFoundException(BOOKS_NOT_FOUND);
        }
        return bookList;
    }

    public Optional<Book> findById(Long id) throws BookNotFoundException {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new BookNotFoundException(BOOK_BY_ID);
        }
        return book;
    }

    public Optional<Book> findByISBN(String ISBN) throws BookNotFoundException {
        Optional<Book> book = bookRepository.findByISBN(ISBN);
        if (book.isEmpty()) {
            throw new BookNotFoundException(BOOK_BY_ISBN);
        }
        return book;
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

    public Book updateBookByISBN(String ISBN, BookDto bookDto) throws BookNotFoundException {
        try {
            Optional<Book> byISBN = findByISBN(ISBN);
            Book book = BookMapper.INSTANCE.bookDtoToBook(bookDto);
            book.setId(byISBN.get().getId());
            return bookRepository.save(book);
        } catch (DataIntegrityViolationException e) {
            throw new BookAlreadyExistsException(BOOK_ALREADY_EXISTS);
        }

    }
}


