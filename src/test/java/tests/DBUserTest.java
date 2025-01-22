package tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Тестовый класс для проверки операций с пользователями в базе данных WordPress.
 */
public class DBUserTest extends DBBaseTest {
    private int userId;
    private final String userLogin = generator.generateRandomWord(10);
    private final String userPass = generator.generateRandomWord(9);
    private final String userNiceName = generator.generateRandomWord(8);
    private final String userEmail = generator.generateRandomEmail();
    private final String userUrl = "http://example.com";
    private final Date userRegistered = new Date(System.currentTimeMillis());
    private final String userActivationKey = generator.generateRandomWord(15);
    private final int userStatus = 1;
    private final String displayName = generator.generateRandomWord(6);

    /**
     * Проверяет создание нового пользователя в базе данных.
     * Проверяет, что вставленный пользователь соответствует ожиданиям.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Создание пользователя")
    @Severity(SeverityLevel.NORMAL)
    void testCreateUser() throws SQLException {
        String insertQuery = "INSERT INTO wp_users (user_login, user_pass, user_nicename, user_email, " +
                "user_url, user_registered, user_activation_key, user_status, display_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, userLogin);
            insertStatement.setString(2, userPass);
            insertStatement.setString(3, userNiceName);
            insertStatement.setString(4, userEmail);
            insertStatement.setString(5, userUrl);
            insertStatement.setDate(6, userRegistered);
            insertStatement.setString(7, userActivationKey);
            insertStatement.setInt(8, userStatus);
            insertStatement.setString(9, displayName);

            insertStatement.executeUpdate();

            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userId= generatedKeys.getInt(1); // Возвращаем ID нового пользователя
                }
            }
        }

        String selectUserQuery = "SELECT * FROM wp_users WHERE ID = ?";
        try (PreparedStatement selectUserStatement = connection.prepareStatement(selectUserQuery)) {
            selectUserStatement.setInt(1, userId);

            try (ResultSet resultSet = selectUserStatement.executeQuery()) {
                    assertTrue(resultSet.next(), "Пользователь не найден в базе данных.");
                    assertEquals(userLogin, resultSet.getString("user_login"));
                    assertEquals(userPass, resultSet.getString("user_pass"));
                    assertEquals(userNiceName, resultSet.getString("user_nicename"));
                    assertEquals(userEmail, resultSet.getString("user_email"));
                    assertEquals(userUrl, resultSet.getString("user_url"));
                    assertEquals(userRegistered.toString(), resultSet.getDate("user_registered").toString());
                    assertEquals(userActivationKey, resultSet.getString("user_activation_key"));
                    assertEquals(userStatus, resultSet.getInt("user_status"));
                    assertEquals(displayName, resultSet.getString("display_name"));
            }
        }
    }

    /**
     * Проверяет обновление существующего пользователя.
     * Проверяет, что поле 'user_nicename' пользователя успешно обновлено.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Обновление пользователя")
    @Severity(SeverityLevel.NORMAL)
    void testUpdateUser() throws SQLException {
        userId = dataManagementUtils.createUserGetId(userLogin, userPass, userNiceName, userEmail, userUrl,
                userRegistered, userActivationKey, userStatus, displayName);

        String updatedNiceName = "Updated Test User";
        String updateQuery = "UPDATE wp_users SET user_nicename = ? WHERE ID = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, updatedNiceName);
            updateStatement.setInt(2, userId);
            updateStatement.executeUpdate();
        }

        String selectQuery = "SELECT user_nicename FROM wp_users WHERE ID = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, userId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertTrue(resultSet.next(), "Пользователь не найден после обновления.");
                assertEquals(updatedNiceName, resultSet.getString("user_nicename"));
            }
        }
    }

    /**
     * Проверяет удаление пользователя из базы данных.
     * Проверяет, что пользователь успешно удален.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Удаление пользователя")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteUser() throws SQLException {
        userId = dataManagementUtils.createUserGetId(userLogin, userPass, userNiceName, userEmail, userUrl,
                userRegistered, userActivationKey, userStatus, displayName);

        String deleteQuery = "DELETE FROM wp_users WHERE ID = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, userId);

            int rowsAffected = deleteStatement.executeUpdate();
            assertTrue(rowsAffected > 0, "Удаление пользователя не выполнено.");
        }

        String selectQuery = "SELECT * FROM wp_users WHERE ID = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, userId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertFalse(resultSet.next(), "Пользователь все еще существует после удаления.");
            }
        }
    }

    /**
     * Проверяет получение пользователя с несуществующим ID.
     * Проверяет, что пользователь с указанным ID не найден.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Получение пользователя с несуществующим ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserWithInvalidId() throws SQLException {
        int nonExistentUserId = 99999;

        String selectQuery = "SELECT * FROM wp_users WHERE ID = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, nonExistentUserId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertFalse(resultSet.next(), "Найден пользователь с несуществующим ID.");
            }
        }
    }
    /**
     * Закрывает соединение с базой данных после каждого теста.
     * Удаляет пользователей, созданных в тестах, если это необходимо.
     *
     * @param testInfo информация о текущем тесте
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @AfterEach
    void closeDatabaseConnection(TestInfo testInfo) throws SQLException {
        if ("testCreateUser()".equals(testInfo.getDisplayName()) || "testUpdateUser()".equals(testInfo.getDisplayName())) {
            dataManagementUtils.deleteUserById(userId);
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                fail("Ошибка при закрытии подключения к базе данных: " + e.getMessage());
            }
        }
    }
}
