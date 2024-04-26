package ua.savchenko.user_management.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.savchenko.user_management.configuration.UserConfiguration;
import ua.savchenko.user_management.exception.ValidatorException;

import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class AgeValidator {

    private final UserConfiguration userConfiguration;

    public void checkForAge(LocalDate birthDate) {
        if (Period.between(birthDate, LocalDate.now()).getYears() < userConfiguration.getMinAge()){
            throw new ValidatorException("User must be at least " + userConfiguration.getMinAge() + " years old");
        }
    }
}
