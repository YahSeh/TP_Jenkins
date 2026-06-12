package com.epsi.tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private static final String ADMIN_USER_ENV = "APP_ADMIN_USER";
    private static final String ADMIN_PASSWORD_ENV = "APP_ADMIN_PASSWORD";
    private static final String DB_URL_ENV = "DB_URL";
    private static final String DB_USER_ENV = "DB_USER";
    private static final String DB_PASSWORD_ENV = "DB_PASSWORD";

    public boolean login(String username, String password) {
        LOGGER.log(Level.INFO, "Tentative de connexion pour l'utilisateur : {0}", username);

        String expectedUsername = System.getenv(ADMIN_USER_ENV);
        String expectedPassword = System.getenv(ADMIN_PASSWORD_ENV);

        if (isBlank(expectedUsername) || isBlank(expectedPassword)) {
            LOGGER.warning("Les identifiants administrateur ne sont pas configures.");
            return false;
        }

        boolean authenticated = expectedUsername.equals(username) && expectedPassword.equals(password);
        if (authenticated) {
            LOGGER.info("Connexion reussie.");
        } else {
            LOGGER.warning("Connexion refusee.");
        }

        return authenticated;
    }

    public List<String> getUserDetails(String username) {
        List<String> users = new ArrayList<>();

        if (isBlank(username)) {
            LOGGER.warning("Le nom d'utilisateur est obligatoire.");
            return users;
        }

        String dbUrl = System.getenv(DB_URL_ENV);
        String dbUser = System.getenv(DB_USER_ENV);
        String dbPassword = System.getenv(DB_PASSWORD_ENV);

        if (isBlank(dbUrl) || isBlank(dbUser) || isBlank(dbPassword)) {
            LOGGER.warning("La connexion a la base de donnees n'est pas configuree.");
            return users;
        }

        String query = "SELECT username FROM users WHERE username = ?";

        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(resultSet.getString("username"));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.WARNING, "Impossible de recuperer les informations utilisateur.", exception);
        }

        return users;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
