package ua.savchenko.user_management.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import ua.savchenko.user_management.configuration.UserConfiguration;
import ua.savchenko.user_management.exception.ValidatorException;

@ExtendWith(MockitoExtension.class)
class AgeValidatorTest {

    @InjectMocks
    private AgeValidator ageValidator;

    @Mock
    private UserConfiguration userConfiguration;

    @BeforeEach
    void setUp() {
        when(userConfiguration.getMinAge()).thenReturn(18);
    }

    @Test
    void testCheckForAgeWhenAgeIsSufficientShouldNotThrowException() {
        LocalDate birthDate = LocalDate.now().minusYears(20);

        assertDoesNotThrow(() -> ageValidator.checkForAge(birthDate));
    }

    @Test
    void testCheckForAgeWhenAgeIsNotSufficientShouldThrowValidatorException() {
        LocalDate birthDate = LocalDate.now().minusYears(16);

        ValidatorException thrown = assertThrows(
                ValidatorException.class,
                () -> ageValidator.checkForAge(birthDate),
                "Expected checkForAge to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("User must be at least 18 years old"));
    }
}
