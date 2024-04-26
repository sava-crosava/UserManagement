package ua.savchenko.user_management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import ua.savchenko.user_management.exception.DuplicateEmailException;
import ua.savchenko.user_management.exception.NotFoundException;
import ua.savchenko.user_management.exception.ValidatorException;
import ua.savchenko.user_management.model.User;
import ua.savchenko.user_management.storage.UserStorage;
import ua.savchenko.user_management.util.AgeValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserStorage userStorage;

    @Mock
    private AgeValidator ageValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userStorage.getUsers()).thenReturn(new HashMap<>());
    }

    @Test
    void testCreateUserValidUserReturnsUser() {
        User user = new User("test@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");

        userService.createUser(user);

        verify(userStorage, times(2)).getUsers();
        verify(ageValidator).checkForAge(user.getBirthDate());
        assertEquals(user, userStorage.getUsers().get(user.getEmail()));
    }

    @Test
    void testCreateUserDuplicateEmailThrowsException() {
        User user = new User("test@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");

        userStorage.getUsers().put(user.getEmail(), user);

        assertThrows(DuplicateEmailException.class, () -> userService.createUser(user));
    }

    @Test
    void testUpdateUserExistingUserUpdatesFields() {
        User originalUser = new User("test@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        userStorage.getUsers().put(originalUser.getEmail(), originalUser);

        User updatedUser = new User(null, "Jane", null, null, null, null);
        userService.updateUser(originalUser.getEmail(), updatedUser);

        assertEquals("Jane", userStorage.getUsers().get(originalUser.getEmail()).getFirstName());
        assertNotNull(userStorage.getUsers().get(originalUser.getEmail()));
    }

    @Test
    void testUpdateUserNonExistingUserThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.updateUser("nonexistent@example.com", new User()));
    }

    @Test
    void testDeleteUserExistingEmailRemovesUser() {
        User user = new User("test@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        userStorage.getUsers().put(user.getEmail(), user);
        userService.deleteUser(user.getEmail());
        assertFalse(userStorage.getUsers().containsKey(user.getEmail()));
    }

    @Test
    void testDeleteUserNonExistingEmailThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser("nonexistent@example.com"));
    }

    @Test
    void testFindUsersByBirthDateRangeValidRangeReturnsUsers() {
        User user1 = new User("user1@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        User user2 = new User("user2@example.com", "Jane", "Doe", LocalDate.of(1995, 5, 5), "456 Lane", "9876543210");
        userStorage.getUsers().put(user1.getEmail(), user1);
        userStorage.getUsers().put(user2.getEmail(), user2);

        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(2001, 1, 1);

        List<User> results = userService.findUsersByBirthDateRange(from, to);

        assertTrue(results.contains(user1));
        assertTrue(results.contains(user2));
        assertEquals(2, results.size());
    }

    @Test
    void testFindUsersByBirthDateRange_InvalidRange_ThrowsValidatorException() {
        LocalDate from = LocalDate.of(2002, 1, 1);
        LocalDate to = LocalDate.of(2001, 1, 1);
        assertThrows(ValidatorException.class, () -> userService.findUsersByBirthDateRange(from, to));
    }
}
