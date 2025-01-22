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
 * Тестовый класс для проверки операций с постами в базе данных WordPress.
 */
public class DBPostTest extends DBBaseTest {
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
     * Проверяет, что вставленный пост соответствует ожиданиям.
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

        String insertQuery = "INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, " +
                "post_title, post_excerpt, post_status, comment_status, ping_status, post_password, post_name, " +
                "to_ping, pinged, post_modified, post_modified_gmt, post_content_filtered, post_parent, guid, " +
                "menu_order, post_type, post_mime_type, comment_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setLong(1, postAuthor);
            insertStatement.setTimestamp(2, postDate);
            insertStatement.setTimestamp(3, postDateGmt);
            insertStatement.setString(4, postContent);
            insertStatement.setString(5, postTitle);
            insertStatement.setString(6, postExcerpt);
            insertStatement.setString(7, postStatus);
            insertStatement.setString(8, commentStatus);
            insertStatement.setString(9, pingStatus);
            insertStatement.setString(10, postPassword);
            insertStatement.setString(11, postName);
            insertStatement.setString(12, toPing);
            insertStatement.setString(13, pinged);
            insertStatement.setTimestamp(14, postModified);
            insertStatement.setTimestamp(15, postModifiedGmt);
            insertStatement.setString(16, postContentFiltered);
            insertStatement.setLong(17, postParent);
            insertStatement.setString(18, guid);
            insertStatement.setInt(19, menuOrder);
            insertStatement.setString(20, postType);
            insertStatement.setString(21, postMimeType);
            insertStatement.setLong(22, commentCount);

            insertStatement.executeUpdate();

            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    postId = generatedKeys.getInt(1);
                }
            }
        }

        String selectPostQuery = "SELECT * FROM wp_posts WHERE ID = ?";
        try (PreparedStatement selectPostStatement = connection.prepareStatement(selectPostQuery)) {
            selectPostStatement.setLong(1, postId);

            try (ResultSet resultSet = selectPostStatement.executeQuery()) {
                assertTrue(resultSet.next(), "Пост не найден в базе данных.");

                assertEquals(postAuthor, resultSet.getLong("post_author"));
                assertEquals(postDate.toLocalDateTime().withNano(0),
                        resultSet.getTimestamp("post_date").toLocalDateTime().withNano(0));
                assertEquals(postDateGmt.toLocalDateTime().withNano(0),
                        resultSet.getTimestamp("post_date_gmt").toLocalDateTime().withNano(0));
                assertEquals(postContent, resultSet.getString("post_content"));
                assertEquals(postTitle, resultSet.getString("post_title"));
                assertEquals(postExcerpt, resultSet.getString("post_excerpt"));
                assertEquals(postStatus, resultSet.getString("post_status"));
                assertEquals(commentStatus, resultSet.getString("comment_status"));
                assertEquals(pingStatus, resultSet.getString("ping_status"));
                assertEquals(postPassword, resultSet.getString("post_password"));
                assertEquals(postName, resultSet.getString("post_name"));
                assertEquals(toPing, resultSet.getString("to_ping"));
                assertEquals(pinged, resultSet.getString("pinged"));
                assertEquals(postModified.toLocalDateTime().withNano(0),
                        resultSet.getTimestamp("post_modified").toLocalDateTime().withNano(0));
                assertEquals(postModifiedGmt.toLocalDateTime().withNano(0),
                        resultSet.getTimestamp("post_modified_gmt").toLocalDateTime().withNano(0));
                assertEquals(postContentFiltered, resultSet.getString("post_content_filtered"));
                assertEquals(postParent, resultSet.getLong("post_parent"));
                assertEquals(guid, resultSet.getString("guid"));
                assertEquals(menuOrder, resultSet.getInt("menu_order"));
                assertEquals(postType, resultSet.getString("post_type"));
                assertEquals(postMimeType, resultSet.getString("post_mime_type"));
                assertEquals(commentCount, resultSet.getLong("comment_count"));
            }
        }
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

        String updatedStatus = "publish";
        String updateQuery = "UPDATE wp_posts SET post_status = ? WHERE ID = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, updatedStatus);
            updateStatement.setInt(2, postId);
            updateStatement.executeUpdate();
        }

        String selectQuery = "SELECT post_status FROM wp_posts WHERE ID = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, postId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertTrue(resultSet.next(), "Пост не найден после обновления.");
                assertEquals(updatedStatus, resultSet.getString("post_status"));
            }
        }
    }

    /**
     * Проверяет удаление поста из базы данных.
     * Проверяет, что пост успешно удалён.
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

        String deleteQuery = "DELETE FROM wp_posts WHERE ID = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, postId);

            int rowsAffected = deleteStatement.executeUpdate();
            assertTrue(rowsAffected > 0, "Удаление поста не выполнено.");
        }

        String selectQuery = "SELECT * FROM wp_posts WHERE ID = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, postId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertFalse(resultSet.next(), "Пост все еще существует после удаления.");
            }
        }
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

        String selectQuery = "SELECT * FROM wp_posts WHERE ID = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, nonExistentUserId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                assertFalse(resultSet.next(), "Найден пост с несуществующим ID.");
            }
        }
    }
    /**
     * Закрывает соединение с базой данных после каждого теста.
     * Удаляет посты, созданные в тестах, если это необходимо.
     *
     * @param testInfo информация о текущем тесте
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @AfterEach
    void closeDatabaseConnection(TestInfo testInfo) throws SQLException {
        if ("testCreatePost()".equals(testInfo.getDisplayName()) || "testUpdatePost()".equals(testInfo.getDisplayName())) {
            dataManagementUtils.deletePostById(postId);
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
