package ua.savchenko.user_management.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "user")
@Getter
@Setter
public class UserConfiguration {
    private int minAge;
}
