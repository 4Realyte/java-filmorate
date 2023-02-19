package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NonNull
    private String name;
   // @NotBlank @Size(message = "максимальная длина описания — 200 символов", max=200)
    private String description;
   // @PastOrPresent()
    @NonNull
    private LocalDate releaseDate;
    //@Positive
    private long duration;
}
