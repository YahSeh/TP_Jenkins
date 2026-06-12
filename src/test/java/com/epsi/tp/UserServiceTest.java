package com.epsi.tp;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    @Test
    void loginShouldReturnFalseWithMissingCredentials() {
        UserService userService = new UserService(Map.<String, String>of()::get);

        assertFalse(userService.login(null, null));
    }

    @Test
    void loginShouldReturnTrueWithValidCredentials() {
        UserService userService = new UserService(Map.of(
                "APP_ADMIN_USER", "admin",
                "APP_ADMIN_PASSWORD", "password"
        )::get);

        assertTrue(userService.login("admin", "password"));
    }

    @Test
    void loginShouldReturnFalseWithInvalidCredentials() {
        UserService userService = new UserService(Map.of(
                "APP_ADMIN_USER", "admin",
                "APP_ADMIN_PASSWORD", "password"
        )::get);

        assertFalse(userService.login("admin", "bad-password"));
    }

    @Test
    void getUserDetailsShouldReturnEmptyListWithInvalidUsername() {
        UserService userService = new UserService(Map.<String, String>of()::get);

        assertTrue(userService.getUserDetails(null).isEmpty());
    }

    @Test
    void getUserDetailsShouldReturnEmptyListWithMissingDatabaseConfiguration() {
        UserService userService = new UserService(Map.of(
                "DB_URL", "jdbc:mysql://localhost:3306/mydb"
        )::get);

        assertTrue(userService.getUserDetails("john_doe").isEmpty());
    }

    @Test
    void getUserDetailsShouldReturnEmptyListWhenDatabaseConnectionFails() {
        UserService userService = new UserService(Map.of(
                "DB_URL", "jdbc:unknown://localhost:3306/mydb",
                "DB_USER", "user",
                "DB_PASSWORD", "password"
        )::get);

        assertTrue(userService.getUserDetails("john_doe").isEmpty());
    }

    @Test
    void getUserDetailsShouldReturnMatchingUser() throws SQLException {
        String dbUrl = "jdbc:h2:mem:user_service_test;MODE=MySQL;DB_CLOSE_DELAY=-1";
        String dbUser = "sa";
        String dbPassword = "password";

        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                Statement statement = connection.createStatement()
        ) {
            statement.execute("DROP TABLE IF EXISTS users");
            statement.execute("CREATE TABLE users (username VARCHAR(255) PRIMARY KEY)");
            statement.execute("INSERT INTO users (username) VALUES ('john_doe')");
        }

        UserService userService = new UserService(Map.of(
                "DB_URL", dbUrl,
                "DB_USER", dbUser,
                "DB_PASSWORD", dbPassword
        )::get);

        assertEquals(1, userService.getUserDetails("john_doe").size());
    }
}
