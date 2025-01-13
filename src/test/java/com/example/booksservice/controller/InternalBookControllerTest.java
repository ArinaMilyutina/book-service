package com.example.booksservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.booksservice.controller.internal.InternalBookController;
import com.example.booksservice.dto.BookRequest;
import com.example.booksservice.dto.BookResponse;
import com.example.booksservice.dto.ListBookResponse;
import com.example.booksservice.entity.Genre;
import com.example.booksservice.entity.Status;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.feignclient.UserClient;
import com.example.booksservice.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.Set;

@WebMvcTest(InternalBookController.class)
class InternalBookControllerTest {
    private static final String TITLE = "The Little Prince";
    private static final String AUTHOR = "Antoine de Saint-Exupery";
    private static final String ISBN = "0034567890123";
    private static final String DESCRIPTION = "The fairy tale tells about a Little Prince who visit various planets";
    private static final String URL_CREATE_BOOK = "/book/create";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    @InjectMocks
    private InternalBookController bookController;
    @MockBean
    private UserClient userClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBook() throws Exception {
        BookRequest bookRequest = createBookDto();
        BookResponse bookResponse = new BookResponse();
        bookResponse.setTitle(TITLE);
        bookResponse.setAuthor(AUTHOR);
        when(userClient.getCurrentUserId()).thenReturn(1L);
        when(bookService.createBook(any(BookRequest.class))).thenReturn(bookResponse);
        ResultActions response = mockMvc.perform(post(URL_CREATE_BOOK)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest))
                .with(csrf()));
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(bookResponse)));
    }

    @Test
    void findAll() throws BookNotFoundException {
        ListBookResponse listBookResponse = new ListBookResponse();
        when(bookService.findAll()).thenReturn(listBookResponse);
        ResponseEntity<ListBookResponse> response = bookController.findAll();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listBookResponse, response.getBody());
    }

    @Test
    void findById() throws BookNotFoundException {
        BookResponse bookResponse = new BookResponse();
        when(bookService.findById(anyLong())).thenReturn(Optional.of(bookResponse));
        ResponseEntity<BookResponse> response = bookController.findById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bookResponse, response.getBody());
    }

    @Test
    void findByISBN() throws BookNotFoundException {
        BookResponse bookResponse = new BookResponse();
        String ISBN = "1234568907653";
        when(bookService.findByISBN(ISBN)).thenReturn(Optional.of(bookResponse));
        ResponseEntity<BookResponse> response = bookController.findByISBN(ISBN);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bookResponse, response.getBody());
    }

    @Test
    void deleteByISBN() throws BookNotFoundException {
        doNothing().when(bookService).deleteBookByISBN(anyString());
        ResponseEntity<String> response = bookController.deleteByISBN(ISBN);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void availableBooks() {
        ListBookResponse listBookResponse = new ListBookResponse();
        when(bookService.availableBooks()).thenReturn(listBookResponse);
        ResponseEntity<ListBookResponse> response = bookController.availableBooks();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listBookResponse, response.getBody());
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
}
