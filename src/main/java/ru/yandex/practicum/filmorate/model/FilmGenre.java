package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FilmGenre {
    @NotBlank
    private String name;
    private int id;
}
