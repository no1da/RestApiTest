package utils;

import java.sql.*;

/**
 * Утилиты для управления базой данных, включая создание и удаление пользователей, постов и тегов.
 */
public class DataManagementUtils {
    private final Connection connection;
    /**
     * Конструктор для инициализации объекта управления данными с заданным соединением.
     *
     * @param connection Соединение с базой данных.
     */
    public DataManagementUtils(Connection connection) {
        this.connection = connection;
    }

    /**
     * Создает пользователя в базе данных с указанными параметрами и возвращает его ID.
     *
     * @param userLogin         Логин пользователя.
     * @param userPass          Пароль пользователя.
     * @param userNiceName      Приятное имя пользователя.
     * @param userEmail         Email пользователя.
     * @param userUrl           URL пользователя.
     * @param userRegistered    Дата регистрации пользователя.
     * @param userActivationKey Ключ активации пользователя.
     * @param userStatus        Статус пользователя.
     * @param displayName       Имя, которое отображается.
     * @return ID созданного пользователя.
     * @throws SQLException Если что-то пошло не так при выполнении SQL-запроса.
     */
    public int createUserGetId(String userLogin, String userPass, String userNiceName, String userEmail, String userUrl,
                               Date userRegistered, String userActivationKey, int userStatus, String displayName) throws SQLException {
        String insertQuery = "INSERT INTO wp_users (user_login, user_pass, user_nicename, user_email, " +
                "user_url, user_registered, user_activation_key, user_status, display_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
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
                    return generatedKeys.getInt(1); // Возвращаем ID нового пользователя
                }
            }
        }
        throw new SQLException("Не удалось получить сгенерированный ID для нового пользователя.");
    }
    /**
     * Создает пост в базе данных и возвращает его ID.
     *
     * @param postAuthor        Автор поста.
     * @param postDate          Дата создания поста.
     * @param postDateGmt      GMT дата создания поста.
     * @param postContent       Содержимое поста.
     * @param postTitle         Заголовок поста.
     * @param postExcerpt       Краткое содержание поста.
     * @param postStatus        Статус поста.
     * @param commentStatus     Статус комментариев.
     * @param pingStatus        Статус пинга.
     * @param postPassword      Пароль поста.
     * @param postName          Имя поста.
     * @param toPing            Список URL для пинга.
     * @param pinged            Список URL, которые были пингованы.
     * @param postModified      Дата модификации поста.
     * @param postModifiedGmt   GMT дата модификации поста.
     * @param postContentFiltered Отфильтрованное содержание поста.
     * @param postParent        ID родительского поста.
     * @param guid              Глобально уникальный идентификатор для поста.
     * @param menuOrder         Порядок в меню.
     * @param postType          Тип поста.
     * @param postMimeType      MIME тип поста.
     * @param commentCount      Количество комментариев к посту.
     * @return ID созданного поста.
     * @throws SQLException Если возникает ошибка во время выполнения SQL-запроса.
     */
    public int createPostGetId(long postAuthor, Timestamp postDate, Timestamp postDateGmt, String postContent,
                               String postTitle, String postExcerpt, String postStatus, String commentStatus, String pingStatus,
                               String postPassword, String postName, String toPing, String pinged, Timestamp postModified,
                               Timestamp postModifiedGmt, String postContentFiltered, long postParent, String guid,
                               int menuOrder, String postType, String postMimeType, long commentCount) throws SQLException {
        String query = "INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, " +
                "post_excerpt, post_status, comment_status, ping_status, post_password, post_name, to_ping, pinged, " +
                "post_modified, post_modified_gmt, post_content_filtered, post_parent, guid, menu_order, post_type, " +
                "post_mime_type, comment_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, postAuthor);
            stmt.setTimestamp(2, postDate);
            stmt.setTimestamp(3, postDateGmt);
            stmt.setString(4, postContent);
            stmt.setString(5, postTitle);
            stmt.setString(6, postExcerpt);
            stmt.setString(7, postStatus);
            stmt.setString(8, commentStatus);
            stmt.setString(9, pingStatus);
            stmt.setString(10, postPassword);
            stmt.setString(11, postName);
            stmt.setString(12, toPing);
            stmt.setString(13, pinged);
            stmt.setTimestamp(14, postModified);
            stmt.setTimestamp(15, postModifiedGmt);
            stmt.setString(16, postContentFiltered);
            stmt.setLong(17, postParent);
            stmt.setString(18, guid);
            stmt.setInt(19, menuOrder);
            stmt.setString(20, postType);
            stmt.setString(21, postMimeType);
            stmt.setLong(22, commentCount);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Не удалось получить сгенерированный ID для нового поста.");
    }
    /**
     * Создает тег в базе данных и возвращает его ID.
     *
     * @param termName   Название тега.
     * @param termSlug   Слаг тега.
     * @param termGroup  Группа тега.
     * @return ID созданного тега.
     * @throws SQLException Если возникает ошибка во время выполнения SQL-запроса.
     */
    public int createTermGetId(String termName, String termSlug, long termGroup) throws SQLException {

        String query = "INSERT INTO wp_terms (name, slug, term_group) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, termName);
            stmt.setString(2, termSlug);
            stmt.setLong(3, termGroup);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Не удалось получить сгенерированный ID для нового тега.");
    }

    /**
     * Получает запись тега из базы данных по его ID.
     *
     * @param id ID тега для выбора.
     * @return ResultSet содержащий данные тега.
     * @throws SQLException Если возникает ошибка при выполнении SQL-запроса.
     */
    public ResultSet selectTermFromDBByID(int id) throws SQLException {
        String selectQuery = "SELECT * FROM wp_terms WHERE term_id = ?";
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        selectStatement.setInt(1, id);

        return selectStatement.executeQuery();
    }

    /**
     * Обновляет тег в базе данных по его ID.
     *
     * @param termId      ID тега, который нужно обновить.
     * @param updatedName Обновленное название тега.
     * @throws SQLException Если возникает ошибка при выполнении SQL-запроса.
     */
    public void updateTermByID(int termId, String updatedName) throws SQLException {
        String updateQuery = "UPDATE wp_terms SET name = ? WHERE term_id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, updatedName);
            updateStatement.setInt(2, termId);
            updateStatement.executeUpdate();
        }
    }

    /**
     * Получает запись из базы данных по её ID.
     *
     * @param id        ID записи для выбора.
     * @param tableName Имя таблицы, из которой нужно выбрать запись.
     * @return ResultSet содержащий данные записи.
     * @throws SQLException Если возникает ошибка при выполнении SQL-запроса.
     */
    public ResultSet selectEntityFromDBByID(int id, String tableName) throws SQLException {
        String selectQuery = "SELECT * FROM " + tableName + " WHERE ID = ?";
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        selectStatement.setInt(1, id);

        return selectStatement.executeQuery();
    }

    /**
     * Обновляет запись в базе данных по её ID.
     *
     * @param termId          ID записи, которую нужно обновить.
     * @param tableName       Имя таблицы, в которой нужно обновить запись.
     * @param variable        Имя переменной, которую нужно обновить.
     * @param updatedVariable Новое значение переменной.
     * @throws SQLException Если возникает ошибка при выполнении SQL-запроса.
     */
    public void updateEntityByID(int termId, String tableName, String variable, String updatedVariable) throws SQLException {
        String updateQuery = "UPDATE " + tableName + " SET " + variable + " = ? WHERE ID = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, updatedVariable);
            updateStatement.setInt(2, termId);
            updateStatement.executeUpdate();
        }
    }

    /**
     * Считывает количество записей в указанной таблице.
     *
     * @param tableName Имя таблицы.
     * @return Количество записей в таблице.
     * @throws SQLException Если возникает ошибка при выполнении SQL-запроса.
     */
    public Long countEntityInDB(String tableName) throws SQLException {
        String countQuery = "SELECT COUNT(*) FROM " + tableName;
        PreparedStatement countStatement = connection.prepareStatement(countQuery);
        ResultSet resultSet = countStatement.executeQuery();

        Long count = null;
        if (resultSet.next()) {
            count = resultSet.getLong(1);
        }

        resultSet.close();
        countStatement.close();
        return count;
    }

    /**
     * Удаляет запись из базы данных по его ID.
     *
     * @param id ID записи.
     * @param tableName Имя таблицы, из которой нужно удалить запись.
     * @throws SQLException Если возникает ошибка во время выполнения SQL-запроса.
     */
    public void deleteEntityById(int id, String tableName) throws SQLException {
        String deleteQuery = "DELETE FROM " + tableName + " WHERE ID = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        }
    }
    /**
     * Удаляет тег из базы данных по его ID.
     *
     * @param termId ID тега.
     * @throws SQLException Если возникает ошибка во время выполнения SQL-запроса.
     */
    public void deleteTagById(int termId) throws SQLException {
        String deleteQuery = "DELETE FROM wp_terms WHERE term_id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, termId);
            deleteStatement.executeUpdate();
        }
    }
}
