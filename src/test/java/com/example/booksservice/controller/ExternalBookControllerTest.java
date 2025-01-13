package com.example.booksservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.booksservice.controller.external.ExternalBookController;
import com.example.booksservice.dto.BookInfoRequest;
import com.example.booksservice.entity.Status;
import com.example.booksservice.exception.BookNotFoundException;
import com.example.booksservice.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ExternalBookControllerTest {
    private static final String TITLE = "The Little Prince";
    private static final Long ID = 1L;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ExternalBookController externalBookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(externalBookController).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void takeTheBook() throws BookNotFoundException {
        BookInfoRequest bookInfoRequest = new BookInfoRequest(1L, TITLE, Collections.singleton(Status.AVAILABLE));
        when(bookService.takeTheBook(ID)).thenReturn(bookInfoRequest);
        ResponseEntity<BookInfoRequest> responseEntity = externalBookController.takeTheBook(ID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(bookInfoRequest, responseEntity.getBody());
        verify(bookService, times(1)).takeTheBook(ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBookStatus() {
        ResponseEntity<Void> responseEntity = externalBookController.updateBookStatus(ID);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(bookService, times(1)).updateBookStatus(ID);
    }


}