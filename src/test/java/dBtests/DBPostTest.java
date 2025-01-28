package dBtests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Тестовый класс для проверки операций с постами в базе данных WordPress.
 */
public class DBPostTest extends DBBaseTest {
    private static final String TABLE_NAME = "wp_posts";
    private int postId;
    private final long postAuthor = 1;
    private final Timestamp postDate = new Timestamp(System.currentTimeMillis());
    private final Timestamp postDateGmt = new Timestamp(System.currentTimeMillis());
    private final String postContent = generator.generateRandomWord(6);
    private final String postTitle = generator.generateRandomWord(7);
    private final String postExcerpt = generator.generateRandomWord(9);
    private final String postStatus = "draft";
    private final String commentStatus = "open";
    private final String pingStatus = "open";
    private final String postPassword = "";
    private final String postName = generator.generateRandomWord(8);
    private final String toPing = "";
    private final String pinged = "";
    private final Timestamp postModified = new Timestamp(System.currentTimeMillis());
    private final Timestamp postModifiedGmt = new Timestamp(System.currentTimeMillis());
    private final String postContentFiltered = "";
    private final long postParent = 0;
    private final String guid = "http://example.com/test-post";
    private final int menuOrder = 0;
    private final String postType = "post";
    private final String postMimeType = "";
    private final long commentCount = 0;

    /**
     * Проверяет создание нового поста в базе данных.
     * Проверяет, количество строк "До" и "После" создания.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Создание поста")
    @Severity(SeverityLevel.NORMAL)
    void testCreatePost() throws SQLException {
        postDate.setNanos(0);
        postDateGmt.setNanos(0);
        postModified.setNanos(0);
        postModifiedGmt.setNanos(0);
        long countBefore = dataManagementUtils.countEntityInDB(TABLE_NAME);
        postId = dataManagementUtils.createPostGetId(postAuthor, postDate, postDateGmt, postContent, postTitle,
                postExcerpt, postStatus, commentStatus, pingStatus, postPassword, postName, toPing, pinged, postModified,
                postModifiedGmt, postContentFiltered, postParent, guid, menuOrder, postType, postMimeType, commentCount);
        long countAfter = dataManagementUtils.countEntityInDB(TABLE_NAME);
        assertEquals(1, countAfter - countBefore);
    }

    /**
     * Проверяет обновление статуса существующего поста.
     * Проверяет, что статус поста успешно обновлен.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Обновление поста")
    @Severity(SeverityLevel.NORMAL)
    void testUpdatePost() throws SQLException {
        postId = dataManagementUtils.createPostGetId(postAuthor, postDate, postDateGmt, postContent, postTitle,
                postExcerpt, postStatus, commentStatus, pingStatus, postPassword, postName, toPing, pinged,
                postModified, postModifiedGmt, postContentFiltered, postParent, guid, menuOrder, postType,
                postMimeType, commentCount);
        String updatedVariable = "publish";
        String variable = "post_status";
        dataManagementUtils.updateEntityByID(postId, TABLE_NAME, variable, updatedVariable);
        try (ResultSet resultSet = dataManagementUtils.selectEntityFromDBByID(postId, TABLE_NAME)) {
            assertTrue(resultSet.next(), "Пост не найден после обновления.");
            assertEquals(updatedVariable, resultSet.getString("post_status"));
        }
    }

    /**
     * Проверяет удаление поста из базы данных.
     * Проверяет, количество строк "До" и "После" удаления.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Удаление поста")
    @Severity(SeverityLevel.NORMAL)
    void testDeletePost() throws SQLException {
        postId = dataManagementUtils.createPostGetId(postAuthor, postDate, postDateGmt, postContent, postTitle,
                postExcerpt, postStatus, commentStatus, pingStatus, postPassword, postName, toPing, pinged,
                postModified, postModifiedGmt, postContentFiltered, postParent, guid, menuOrder, postType,
                postMimeType, commentCount);
        long countBefore = dataManagementUtils.countEntityInDB(TABLE_NAME);
        dataManagementUtils.deleteEntityById(postId, TABLE_NAME);
        long countAfter = dataManagementUtils.countEntityInDB(TABLE_NAME);
        assertEquals(-1, countAfter - countBefore);
    }

    /**
     * Проверяет попытку получения поста с несуществующим ID.
     * Проверяет, что пост с указанным ID не найден.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @Test
    @DisplayName("Получение поста с несуществующим ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPostWithInvalidId() throws SQLException {
        int nonExistentUserId = 99999;
        try (ResultSet resultSet = dataManagementUtils.selectEntityFromDBByID(nonExistentUserId, TABLE_NAME)) {
            assertFalse(resultSet.next(), "Найден пост с несуществующим ID.");
        }
    }

    /**
     * Закрывает соединение с базой данных после каждого теста.
     * Удаляет посты, созданные в тестах.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @AfterEach
    void closeDatabaseConnection() throws SQLException {
        dataManagementUtils.deleteEntityById(postId, TABLE_NAME);
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                fail("Ошибка при закрытии подключения к базе данных: " + e.getMessage());
            }
        }
    }
}
