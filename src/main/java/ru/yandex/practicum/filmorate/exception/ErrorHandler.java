package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "ru.yandex.practicum.filmorate.controllers")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationEx(final MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации: {}", ex.getFieldError().getDefaultMessage());
        return Map.of("Ошибка валидации", ex.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundEx(final RuntimeException ex) {
        log.warn("Ошибка запроса: {}", ex.getMessage());
        return Map.of("Ошибка запроса", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAnyException(final Throwable ex) {
        log.warn("Неизвестная ошибка: {}", ex.getMessage());
        return Map.of("Произошла неизвестная ошибка", ex.getMessage());
    }
}
