package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.DataManagementUtils;
import utils.RandomGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Абстрактный базовый класс для тестов, работающих с базой данных.
 */
public abstract class DBBaseTest {
    protected Connection connection;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/wordpress";
    private static final String JDBC_USER = "wordpress";
    private static final String JDBC_PASSWORD = "wordpress";

    protected DataManagementUtils dataManagementUtils;

    protected static RandomGenerator generator;
    /**
     * Инициализирует генератор случайных данных перед выполнением тестов.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
        generator = new RandomGenerator();
    }

    /**
     * Устанавливает соединение с базой данных перед каждым тестом.
     *
     * @throws SQLException если возникает ошибка доступа к базе данных
     */
    @BeforeEach
    void setupDatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        dataManagementUtils = new DataManagementUtils(connection);
    }
}
