package dBtests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Тестовый класс для проверки операций с тегами в базе данных WordPress.
 */
public class DBTermTest extends DBBaseTest {
    private static final String TABLE_NAME = "wp_terms";
    private int termId;
    private final String updatedName = generator.generateRandomWord(6);
    private final String termName = generator.generateRandomWord(7);
    private final String termSlug = generator.generateRandomWord(8);
    private final long termGroup = 0;

    /**
     * Проверяет создание нового тега в базе данных.
     * Проверяет, количество строк "До" и "После" создания.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Создание тега")
    @Severity(SeverityLevel.NORMAL)
    void testCreateTerm() throws SQLException {
        long countBefore = dataManagementUtils.countEntityInDB(TABLE_NAME);
        termId = dataManagementUtils.createTermGetId(termName, termSlug, termGroup);
        long countAfter = dataManagementUtils.countEntityInDB(TABLE_NAME);
        assertTrue(countAfter > countBefore);
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

        dataManagementUtils.updateTermByID(termId, updatedName);

        try (ResultSet resultSet = dataManagementUtils.selectTermFromDBByID(termId)) {
            assertTrue(resultSet.next(), "Тег не найден после обновления.");
            assertEquals(updatedName, resultSet.getString("name"));
        }
    }

    /**
     * Проверяет удаление тега из базы данных.
     * Проверяет, количество строк "До" и "После" удаления.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Удаление тега")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteTerm() throws SQLException {
        termId = dataManagementUtils.createTermGetId(termName, termSlug, termGroup);
        long countBefore = dataManagementUtils.countEntityInDB(TABLE_NAME);
        dataManagementUtils.deleteTagById(termId);
        long countAfter = dataManagementUtils.countEntityInDB(TABLE_NAME);
        assertTrue(countAfter < countBefore);
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

        try (ResultSet resultSet = dataManagementUtils.selectTermFromDBByID(nonExistentTermId)) {
            assertFalse(resultSet.next(), "Найден тег с несуществующим ID.");
        }
    }
    /**
     * Закрывает соединение с базой данных после каждого теста.
     * Удаляет теги, созданные в тестах.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @AfterEach
    void closeDatabaseConnection() throws SQLException {
        dataManagementUtils.deleteTagById(termId);
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                fail("Ошибка при закрытии подключения к базе данных: " + e.getMessage());
            }
        }
    }
}