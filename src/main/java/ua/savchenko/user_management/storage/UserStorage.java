package ua.savchenko.user_management.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ua.savchenko.user_management.model.User;

import java.util.HashMap;

@Component
@Getter
@Setter
public class UserStorage {

    private HashMap<String, User> users;

    public UserStorage() {
        users = new HashMap<>();
    }
}
