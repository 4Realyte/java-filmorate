package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum MPA {
    G(1, "G"),
    PG(2, "PG"),
    PG13(3, "PG-13"),
    R(4, "R"),
    NC17(5, "NC-17");
    private int id;
    private String name;

    private MPA(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonCreator
    static MPA findValue(MpaTypeHelper helper) {
        return Arrays.stream(MPA.values())
                .filter(value -> value.id == helper.getId())
                .findFirst()
                .orElseThrow(() -> new MpaNotFoundException(String.format("MPA с ID: %s не обнаружен", helper.getId())));
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MpaTypeHelper {
        private int id;
    }

}
