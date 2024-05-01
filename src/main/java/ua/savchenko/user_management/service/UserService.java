package ua.savchenko.user_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.savchenko.user_management.exception.DuplicateEmailException;
import ua.savchenko.user_management.exception.NotFoundException;
import ua.savchenko.user_management.exception.ValidatorException;
import ua.savchenko.user_management.model.User;
import ua.savchenko.user_management.storage.UserStorage;
import ua.savchenko.user_management.util.AgeValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final AgeValidator ageValidator;

    //  Create user
    public User createUser(User user) {
        ageValidator.checkForAge(user.getBirthDate());

        if (userStorage.getUsers().containsKey(user.getEmail())) {
            throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists.");
        }
        userStorage.getUsers().put(user.getEmail(), user);

        return user;
    }

    //  Update one / some user fields and can also
    //  Update all user fields
    public User updateUser(String email, User updatedUser) {
        if (!userStorage.getUsers().containsKey(email)) {
            throw new NotFoundException("User not found");
        }

        User oldUser = userStorage.getUsers().get(email);

        Optional.ofNullable(updatedUser.getEmail()).ifPresent(e -> {
            userStorage.getUsers().remove(email);
            oldUser.setEmail(e);
            userStorage.getUsers().put(e, oldUser);
        });

        Optional.ofNullable(updatedUser.getFirstName()).ifPresent(oldUser::setFirstName);
        Optional.ofNullable(updatedUser.getLastName()).ifPresent(oldUser::setLastName);
        Optional.ofNullable(updatedUser.getBirthDate()).ifPresent(oldUser::setBirthDate);
        Optional.ofNullable(updatedUser.getAddress()).ifPresent(oldUser::setAddress);
        Optional.ofNullable(updatedUser.getPhoneNumber()).ifPresent(oldUser::setPhoneNumber);

        return oldUser;
    }

    public User replaceUser(String email, User updatedUser) {
        if (!userStorage.getUsers().containsKey(email)) {
            throw new NotFoundException("User not found");
        }

        userStorage.getUsers().remove(email);

        userStorage.getUsers().put(updatedUser.getEmail(), updatedUser);

        return updatedUser;
    }

    // Deleting user
    public void deleteUser(String email) {
        if (!userStorage.getUsers().containsKey(email)) {
            throw new NotFoundException("User not found");
        }

        userStorage.getUsers().remove(email);
    }

    // Search for users by birth date range
    public List<User> findUsersByBirthDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new ValidatorException("'From' date must be before 'To' date");
        }
        return userStorage.getUsers().values().stream()
                .filter(u -> (u.getBirthDate().isEqual(from) || u.getBirthDate().isAfter(from)) &&
                        (u.getBirthDate().isEqual(to) || u.getBirthDate().isBefore(to)))
                .toList();
    }
}

