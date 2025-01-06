package com.example.booksservice.mapper;

import com.example.booksservice.dto.BookRequest;
import com.example.booksservice.dto.BookResponse;
import com.example.booksservice.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    Book bookDtoToBook(BookRequest bookRequest);

    BookResponse bookToBookDto(Book book);

    Book bookDtoToBook(BookResponse bookResponse);

}