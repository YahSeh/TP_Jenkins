package com.epsi.tp;

import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(String[] args) {
        LOGGER.info("Demarrage de l'application...");

        UserService userService = new UserService();

        String username = System.getenv("APP_LOGIN_USER");
        String password = System.getenv("APP_LOGIN_PASSWORD");

        if (username != null && password != null) {
            userService.login(username, password);
        } else {
            LOGGER.info("Aucun identifiant de connexion fourni.");
        }

        userService.getUserDetails("john_doe");
    }
}
