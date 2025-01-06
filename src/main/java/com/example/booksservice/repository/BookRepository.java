package com.example.booksservice.repository;

import com.example.booksservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByISBN(String ISBN);

    void deleteByISBN(String ISBN);

}
