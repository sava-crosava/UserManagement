package ua.savchenko.user_management.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.savchenko.user_management.model.User;
import ua.savchenko.user_management.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{email}")
    public User updateUser(@PathVariable @Email @Valid String email,
                           @Valid @RequestBody User updatedUser) {
        return userService.updateUser(email, updatedUser);
    }

    @PutMapping("/{email}")
    public User replaceUser(@PathVariable @Email @Valid String email,
                            @Valid @RequestBody User updatedUser) {
        return userService.replaceUser(email, updatedUser);
    }

    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable @Email @Valid String email) {
        userService.deleteUser(email);
    }

    @GetMapping("/search")
    public List<User> searchUsersByBirthDateRange(@RequestParam("from") LocalDate from,
                                                  @RequestParam("to") LocalDate to) {
        return userService.findUsersByBirthDateRange(from, to);
    }
}