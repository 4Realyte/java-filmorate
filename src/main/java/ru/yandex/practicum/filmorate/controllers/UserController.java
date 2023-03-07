package ru.yandex.practicum.filmorate.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private UserStorage userStorage;
    private UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable
                            @PositiveOrZero(message = "Параметр id не может быть отрицательным") Integer id) {
        return userStorage.getUserById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                          Integer userId,
                          @PathVariable
                          Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                             Integer userId,
                             @PathVariable
                             Integer friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable("id")
                                       @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                                       Integer userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id")
                                              @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                                              Integer userId,
                                              @PathVariable("otherId")
                                              @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                                              Integer friendId) {
        return userService.findCommonFriends(userId, friendId);
    }
}
