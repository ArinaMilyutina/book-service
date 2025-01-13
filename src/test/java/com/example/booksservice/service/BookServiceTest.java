package com.example.booksservice.service;

import com.example.booksservice.dto.BookInfoRequest;
import com.example.booksservice.dto.BookRequest;
import com.example.booksservice.dto.BookResponse;
import com.example.booksservice.dto.ListBookResponse;
import com.example.booksservice.entity.Book;
import com.example.booksservice.entity.Genre;
import com.example.booksservice.entity.Status;
import com.example.booksservice.exception.BookAlreadyExistsException;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    private static final Long ID = 1L;
    private static final String TITLE = "The Little Prince";
    private static final String AUTHOR = "Antoine de Saint-Exupery";
    private static final String ISBN = "1234567890123";
    private static final String DESCRIPTION = "The fairy tale tells about a Little Prince who visit various planets in space, including Earth";
    private final static String BOOK_ALREADY_EXISTS = "The book with this isbn already exists!!!";
    private final static String BOOKS_NOT_FOUND = "Book not found.";
    private final static String BOOK_BY_ID = "A book with this id not found.";
    private final static String BOOK_BY_ISBN = "A book with isbn not found.";
    private final static String DELETE_BOOK_BY_ISBN = "The book has been deleted.";
    private final static String AVAILABLE_BOOKS = "There are no available books!";


    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createBook_Success() {
        BookRequest bookRequest = createBookDto();
        Book book = createBook(bookRequest);
        when(bookRepository.findByISBN(book.getISBN())).thenReturn(Optional.empty());
        when(bookRepository.save(book)).thenReturn(book);
        BookResponse response = bookService.createBook(bookRequest);
        assertNotNull(response);
        assertEquals(TITLE, response.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void createBook_AlreadyExists() {
        BookRequest bookRequest = createBookDto();
        Book book = createBook(bookRequest);
        when(bookRepository.findByISBN(book.getISBN())).thenReturn(Optional.of(book));
        Exception exception = assertThrows(BookAlreadyExistsException.class, () -> {
            bookService.createBook(bookRequest);
        });
        assertEquals(BOOK_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    public void findAll_Success() {
        List<Book> books = new ArrayList<>();
        books.add(createBook(createBookDto()));
        books.add(createBook(createBookDto()));
        when(bookRepository.findAll()).thenReturn(books);
        ListBookResponse response = bookService.findAll();
        assertNotNull(response);
        assertEquals(2, response.getResponseList().size());
    }

    @Test
    public void findAll_NoBooks() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.findAll();
        });
        assertEquals(BOOKS_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void findById_Success() {
        Book book = createBook(createBookDto());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Optional<BookResponse> response = bookService.findById(1L);
        assertTrue(response.isPresent());
        assertEquals(TITLE, response.get().getTitle());
    }

    @Test
    public void findById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.findById(1L);
        });
        assertEquals(BOOK_BY_ID, exception.getMessage());
    }

    @Test
    public void findByISBN_Success() {
        Book book = createBook(createBookDto());
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(book));
        Optional<BookResponse> response = bookService.findByISBN(ISBN);
        assertTrue(response.isPresent());
        assertEquals(TITLE, response.get().getTitle());
    }

    @Test
    public void findByISBN_NotFound() {
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.findByISBN(ISBN);
        });
        assertEquals(BOOK_BY_ISBN, exception.getMessage());
    }

    @Test
    public void takeTheBook_Success() {
        Book book = createBook(createBookDto());
        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        BookInfoRequest request = bookService.takeTheBook(ID);
        assertNotNull(request);
        assertEquals(TITLE, request.getTitle());
    }

    @Test
    public void takeTheBook_NotFound() {
        when(bookRepository.findById(ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.takeTheBook(ID);
        });
        assertEquals(BOOK_BY_ISBN, exception.getMessage());
    }

    @Test
    public void deleteBookByISBN_Success() {
        Book book = createBook(createBookDto());
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(book));
        bookService.deleteBookByISBN(ISBN);
        verify(bookRepository, times(1)).deleteByISBN(ISBN);
    }

    @Test
    public void deleteBookByISBN_NotFound() {
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBookByISBN(ISBN);
        });
        assertEquals(BOOKS_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void updateBookByISBN_Success() {
        String ISBN = "1234567890758";
        BookRequest bookRequest = createBookDto();
        Book existingBook = createBook(bookRequest);
        Book updatedBook = new Book();
        updatedBook.setISBN(ISBN);
        updatedBook.setTitle(bookRequest.getTitle());
        updatedBook.setAuthor(bookRequest.getAuthor());
        updatedBook.setDescription(bookRequest.getDescription());
        updatedBook.setGenre(bookRequest.getGenre());
        updatedBook.setStatus(bookRequest.getStatus());
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(existingBook)); // Найти существующую книгу
        when(bookRepository.findByISBN(existingBook.getISBN())).thenReturn(Optional.empty()); // Проверка, что другой книги с тем же ISBN нет
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook); // Сохранить обновлённую книгу
        BookResponse response = bookService.updateBookByISBN(ISBN, bookRequest);
        assertNotNull(response);
        assertEquals(bookRequest.getTitle(), response.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void updateBookByISBN_NotFound() {
        BookRequest bookRequest = createBookDto();
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBookByISBN(ISBN, bookRequest);
        });
        assertEquals(BOOK_BY_ISBN, exception.getMessage());
    }

    @Test
    public void updateBookStatus_Success() {
        Book book = createBook(createBookDto());
        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        bookService.updateBookStatus(ID);
        assertTrue(book.getStatus().contains(Status.UNAVAILABLE));
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void availableBooks_Success() {
        List<Book> books = new ArrayList<>();
        books.add(createBook(createBookDto()));
        books.add(createBook(createBookDto()));
        when(bookRepository.findByStatusContaining(Status.AVAILABLE)).thenReturn(books);
        ListBookResponse response = bookService.availableBooks();
        assertNotNull(response);
        assertEquals(2, response.getResponseList().size());
    }

    @Test
    public void availableBooks_NoBooks() {
        when(bookRepository.findByStatusContaining(Status.AVAILABLE)).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.availableBooks();
        });
        assertEquals(AVAILABLE_BOOKS, exception.getMessage());
    }


    private BookRequest createBookDto() {
        BookRequest bookDto = new BookRequest();
        bookDto.setTitle(TITLE);
        bookDto.setAuthor(AUTHOR);
        bookDto.setISBN(ISBN);
        bookDto.setDescription(DESCRIPTION);
        bookDto.setGenre(Set.of(Genre.FANTASY, Genre.TALE));
        bookDto.setStatus(Set.of(Status.AVAILABLE));
        return bookDto;
    }

    private Book createBook(BookRequest bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setISBN(bookDto.getISBN());
        book.setDescription(bookDto.getDescription());
        book.setGenre(bookDto.getGenre());
        book.setStatus(bookDto.getStatus());
        return book;
    }
}
