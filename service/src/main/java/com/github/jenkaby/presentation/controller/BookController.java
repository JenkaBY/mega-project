package com.github.jenkaby.presentation.controller;

import com.github.jenkaby.persistance.entity.BookEntity;
import com.github.jenkaby.persistance.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/test/book")
@RestController
public class BookController {

    private final BookRepository repository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BookEntity> getAll() {
        log.info("Get all books");
        return repository.findAll();
    }
}
