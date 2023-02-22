package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @NotBlank(message = "Электронная почта не может быть пустой") @Email(message = "Эмейл должен быть корректным")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @PastOrPresent(message = "дата рождения не может быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
