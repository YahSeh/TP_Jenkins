package com.epsi.tp;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest {

    @Test
    public void loginShouldReturnFalseWithMissingCredentials() {
        UserService userService = new UserService(Map.<String, String>of()::get);

        assertFalse(userService.login(null, null));
    }

    @Test
    public void loginShouldReturnTrueWithValidCredentials() {
        UserService userService = new UserService(Map.of(
                "APP_ADMIN_USER", "admin",
                "APP_ADMIN_PASSWORD", "password"
        )::get);

        assertTrue(userService.login("admin", "password"));
    }

    @Test
    public void loginShouldReturnFalseWithInvalidCredentials() {
        UserService userService = new UserService(Map.of(
                "APP_ADMIN_USER", "admin",
                "APP_ADMIN_PASSWORD", "password"
        )::get);

        assertFalse(userService.login("admin", "bad-password"));
    }

    @Test
    public void getUserDetailsShouldReturnEmptyListWithInvalidUsername() {
        UserService userService = new UserService(Map.<String, String>of()::get);

        assertTrue(userService.getUserDetails(null).isEmpty());
    }

    @Test
    public void getUserDetailsShouldReturnEmptyListWithMissingDatabaseConfiguration() {
        UserService userService = new UserService(Map.of(
                "DB_URL", "jdbc:mysql://localhost:3306/mydb"
        )::get);

        assertTrue(userService.getUserDetails("john_doe").isEmpty());
    }

    @Test
    public void getUserDetailsShouldReturnEmptyListWhenDatabaseConnectionFails() {
        UserService userService = new UserService(Map.of(
                "DB_URL", "jdbc:unknown://localhost:3306/mydb",
                "DB_USER", "user",
                "DB_PASSWORD", "password"
        )::get);

        assertTrue(userService.getUserDetails("john_doe").isEmpty());
    }
}
