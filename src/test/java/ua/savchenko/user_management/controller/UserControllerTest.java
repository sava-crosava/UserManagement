package ua.savchenko.user_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.savchenko.user_management.exception.BusinessExceptionHandler;
import ua.savchenko.user_management.model.User;
import ua.savchenko.user_management.service.UserService;
import ua.savchenko.user_management.exception.NotFoundException;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({BusinessExceptionHandler.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User("test@example.com", "John", "Doe", LocalDate.of(1980, 1, 1), "123 Test St", "1234567890");
    }

    @Test
    void testCreateUserShouldReturnUserWithEmail() throws Exception {
        given(userService.createUser(any(User.class))).willReturn(user);
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void testCreateUserWithInvalidFieldsShouldReturnBadRequest() throws Exception {
        User invalidUser = new User("", "", "", LocalDate.now().plusDays(1), "", "");
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserByEmailShouldReturnUpdatedUser() throws Exception {
        given(userService.updateUser(eq("test@example.com"), any(User.class))).willReturn(user);
        mockMvc.perform(put("/api/user/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void testUpdateUserWhenUserNotFoundShouldReturnNotFoundStatus() throws Exception {
        willThrow(new NotFoundException("User not found")).given(userService).updateUser(eq("not_found@example.com"), any(User.class));
        mockMvc.perform(put("/api/user/{email}", "not_found@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserByEmailShouldReturnOkStatus() throws Exception {
        willDoNothing().given(userService).deleteUser("test@example.com");
        mockMvc.perform(delete("/api/user/{email}", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserWhenUserNotFoundShouldReturnNotFoundStatus() throws Exception {
        willThrow(new NotFoundException("User not found")).given(userService).deleteUser("not_found@example.com");
        mockMvc.perform(delete("/api/user/{email}", "not_found@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchUsersByBirthDateRange() throws Exception {
        LocalDate from = LocalDate.of(1970, 1, 1);
        LocalDate to = LocalDate.of(1990, 12, 31);
        given(userService.findUsersByBirthDateRange(from, to)).willReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/user/search")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));

        verify(userService).findUsersByBirthDateRange(from, to);

    }
}
