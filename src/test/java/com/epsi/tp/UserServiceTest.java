package com.epsi.tp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void loginShouldReturnFalseWithMissingCredentials() {
        UserService userService = new UserService();

        assertFalse(userService.login(null, null));
    }

    @Test
    public void getUserDetailsShouldReturnEmptyListWithInvalidUsername() {
        UserService userService = new UserService();

        assertTrue(userService.getUserDetails(null).isEmpty());
    }
}
