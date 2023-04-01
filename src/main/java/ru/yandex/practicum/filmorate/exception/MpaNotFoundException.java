package ru.yandex.practicum.filmorate.exception;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException() {
    }

    public MpaNotFoundException(String message) {
        super(message);
    }
}
