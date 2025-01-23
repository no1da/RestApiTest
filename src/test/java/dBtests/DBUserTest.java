package dBtests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Тестовый класс для проверки операций с пользователями в базе данных WordPress.
 */
public class DBUserTest extends DBBaseTest {
    private static final String TABLE_NAME = "wp_users";
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
     * Проверяет, количество строк "До" и "После" создания.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Создание пользователя")
    @Severity(SeverityLevel.NORMAL)
    void testCreateUser() throws SQLException {
        long countBefore = dataManagementUtils.countEntityInDB(TABLE_NAME);
        userId = dataManagementUtils.createUserGetId(userLogin, userPass, userNiceName, userEmail, userUrl, userRegistered, userActivationKey, userStatus, displayName);
        long countAfter = dataManagementUtils.countEntityInDB(TABLE_NAME);
        assertTrue(countAfter > countBefore);
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

        String updatedVariable = "Updated Test User";
        String variable = "user_nicename";

        dataManagementUtils.updateEntityByID(userId, TABLE_NAME, variable, updatedVariable);

        try (ResultSet resultSet = dataManagementUtils.selectEntityFromDBByID(userId, TABLE_NAME)) {
            assertTrue(resultSet.next(), "Пользователь не найден после обновления.");
            assertEquals(updatedVariable, resultSet.getString("user_nicename"));
        }
    }

    /**
     * Проверяет удаление пользователя из базы данных.
     * Проверяет, количество строк "До" и "После" удаления.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Удаление пользователя")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteUser() throws SQLException {
        userId = dataManagementUtils.createUserGetId(userLogin, userPass, userNiceName, userEmail, userUrl,
                userRegistered, userActivationKey, userStatus, displayName);
        long countBefore = dataManagementUtils.countEntityInDB(TABLE_NAME);
        dataManagementUtils.deleteEntityById(userId, TABLE_NAME);
        long countAfter = dataManagementUtils.countEntityInDB(TABLE_NAME);
        assertTrue(countAfter < countBefore);
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
        try (ResultSet resultSet = dataManagementUtils.selectEntityFromDBByID(nonExistentUserId, TABLE_NAME)) {
            assertFalse(resultSet.next(), "Найден пользователь с несуществующим ID.");
        }
    }
    /**
     * Закрывает соединение с базой данных после каждого теста.
     * Удаляет пользователей, созданных в тестах.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @AfterEach
    void closeDatabaseConnection() throws SQLException {
        dataManagementUtils.deleteEntityById(userId, TABLE_NAME);
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                fail("Ошибка при закрытии подключения к базе данных: " + e.getMessage());
            }
        }
    }
}
