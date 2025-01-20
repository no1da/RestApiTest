package tests;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import utils.Config;
import utils.RandomGenerator;
@Epic("Testing WordPressAPI")
public abstract class BaseTest {
    protected static RequestSpecification requestSpec;
    protected static RandomGenerator generator;
    protected static int checkedId;
    protected static Config config;
    protected static String fakeID;
    protected static String apiUsers;
    protected static String apiTags;
    protected static String apiPosts;
    /**
     * Инициализирует конфигурационные данные.
     * Выполняется один раз перед всеми тестами.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
        config=new Config();
        generator = new RandomGenerator();
        RestAssured.baseURI = config.getProperty("baseURI");
        fakeID = config.getProperty("fakeId");
    }
}
