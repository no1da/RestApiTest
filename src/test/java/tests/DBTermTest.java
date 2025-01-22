package tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Тестовый класс для проверки операций с тегами в базе данных WordPress.
 */
public class DBTermTest extends DBBaseTest {
    private int termId;
    private final String termName = generator.generateRandomWord(7);
    private final String termSlug = generator.generateRandomWord(8);
    private final long termGroup = 0;

    /**
     * Проверяет создание нового тега в базе данных.
     * Проверяет, что вставленный тег соответствует ожиданиям.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Создание тега")
    @Severity(SeverityLevel.NORMAL)
    void testCreateTerm() throws SQLException {
        String insertQuery = "INSERT INTO wp_terms (name, slug, term_group) VALUES (?, ?, ?)";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, termName);
            insertStatement.setString(2, termSlug);
            insertStatement.setLong(3, termGroup);

            insertStatement.executeUpdate();

            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    termId = generatedKeys.getInt(1);
                }
            }
        }

        String selectQuery = "SELECT * FROM wp_terms WHERE term_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, termId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertTrue(resultSet.next(), "Тег не найден в базе данных.");
                assertEquals(termName, resultSet.getString("name"));
                assertEquals(termSlug, resultSet.getString("slug"));
                assertEquals(termGroup, resultSet.getLong("term_group"));
            }
        }
    }

    /**
     * Проверяет обновление существующего тега.
     * Проверяет, что название тега успешно обновлено.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Обновление тега")
    @Severity(SeverityLevel.NORMAL)
    void testUpdateTerm() throws SQLException {
        termId = dataManagementUtils.createTermGetId(termName, termSlug, termGroup);

        String updatedName = "Updated Term Name";
        String updateQuery = "UPDATE wp_terms SET name = ? WHERE term_id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, updatedName);
            updateStatement.setInt(2, termId);
            updateStatement.executeUpdate();
        }

        String selectQuery = "SELECT name FROM wp_terms WHERE term_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, termId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertTrue(resultSet.next(), "Тег не найден после обновления.");
                assertEquals(updatedName, resultSet.getString("name"));
            }
        }
    }

    /**
     * Проверяет удаление тега из базы данных.
     * Проверяет, что тег успешно удален.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Удаление тега")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteTerm() throws SQLException {
        termId = dataManagementUtils.createTermGetId(termName, termSlug, termGroup);

        String deleteQuery = "DELETE FROM wp_terms WHERE term_id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, termId);

            int rowsAffected = deleteStatement.executeUpdate();
            assertTrue(rowsAffected > 0, "Удаление тега не выполнено.");
        }

        String selectQuery = "SELECT * FROM wp_terms WHERE term_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, termId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertFalse(resultSet.next(), "Тег все еще существует после удаления.");
            }
        }
    }

    /**
     * Проверяет получение тега с несуществующим ID.
     * Проверяет, что тег с указанным ID не найден.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Получение тега с несуществующим ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetTermWithInvalidId() throws SQLException {
        int nonExistentTermId = 99999;

        String selectQuery = "SELECT * FROM wp_terms WHERE term_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, nonExistentTermId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertFalse(resultSet.next(), "Найден тег с несуществующим ID.");
            }
        }
    }
    /**
     * Закрывает соединение с базой данных после каждого теста.
     * Удаляет теги, созданные в тестах, если это необходимо.
     *
     * @param testInfo информация о текущем тесте
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @AfterEach
    void closeDatabaseConnection(TestInfo testInfo) throws SQLException {
        if ("testCreateTerm()".equals(testInfo.getDisplayName()) || "testUpdateTerm()".equals(testInfo.getDisplayName())) {
            dataManagementUtils.deleteTagById(termId);
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