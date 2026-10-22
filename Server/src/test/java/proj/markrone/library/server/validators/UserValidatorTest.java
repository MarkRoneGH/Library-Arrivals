package proj.markrone.library.server.validators;

import proj.markrone.library.server.model.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private UserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserValidator();
    }

    @Test
    void validUser_returnsTrue() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("$2a$10$hashedpassword");

        assertTrue(validator.isValid(user));
    }

    @Test
    void userWithNullUsername_returnsFalse() {
        User user = new User();
        user.setUsername(null);
        user.setPasswordHash("$2a$10$hashedpassword");

        assertFalse(validator.isValid(user));
    }

    @Test
    void userWithEmptyPassword_returnsFalse() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("");

        assertFalse(validator.isValid(user));
    }
}
