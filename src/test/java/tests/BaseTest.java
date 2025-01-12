package tests;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import utils.Config;
/**
 * Основной класс для тестирования WordPressAPI.
 * Этот класс предоставляет общие методики и настройки для тестов.
 */
import static io.restassured.RestAssured.given;
@Epic("Testing WordPressAPI")
public class BaseTest {
    protected static RequestSpecification requestSpec;
    protected static int checkedId;
    protected static Config config;
    protected static String fakeID;
    protected static String forceDelete;
    /**
     * Инициализирует конфигурационные данные.
     * Выполняется один раз перед всеми тестами.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
        config=new Config();
        RestAssured.baseURI = config.getProperty("baseURI");
        requestSpec = given()
                .header("Authorization", config.getProperty("token"))
                .contentType(ContentType.URLENC);
        fakeID = config.getProperty("fakeId");
        forceDelete = config.getProperty("force.delete");
    }
}
