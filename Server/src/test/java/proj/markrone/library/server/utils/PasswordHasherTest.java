package proj.markrone.library.server.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashPassword_returnsNonNullBCryptHash() {
        String hash = PasswordHasher.hashPassword("mySecret123");

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"));
    }

    @Test
    void checkPassword_correctPassword_returnsTrue() {
        String password = "mySecret123";
        String hash = PasswordHasher.hashPassword(password);

        assertTrue(PasswordHasher.checkPassword(password, hash));
    }

    @Test
    void checkPassword_wrongPassword_returnsFalse() {
        String hash = PasswordHasher.hashPassword("mySecret123");

        assertFalse(PasswordHasher.checkPassword("wrongPassword", hash));
    }
}
