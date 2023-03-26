package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.validators.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Jacksonized
public class Film {
    @Builder.Default
    private Set<Integer> usersLiked = new HashSet<>();
    private int id;
    @Builder.Default
    private Set<FilmGenre> genres = new HashSet<>();
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotBlank
    @Size(message = "максимальная длина описания — 200 символов", max = 200)
    private String description;
    @ReleaseDate(message = "дата релиза — не раньше 28.12.1895")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной или равной нулю")
    private long duration;
    private MPA mpaRating;
}
