package tests;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import utils.Config;
import utils.DeleteData;
import utils.RandomGenerator;

import static io.restassured.RestAssured.given;
@Epic("Testing WordPressAPI")
public class BaseTest {
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
        requestSpec = given()
                .header("Authorization", config.getProperty("token"))
                .contentType(ContentType.URLENC);
        fakeID = config.getProperty("fakeId");
    }

    /**
     * Метод, выполняющийся после каждого теста. Удаляет созданные данные.
     *
     * @param testInfo информация о текущем тесте
     */
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        if ("testUpdateUserById()".equals(testInfo.getDisplayName()) || "testPostUserAndGetById()".equals(testInfo.getDisplayName())) {
            DeleteData.deleteUserById(checkedId, requestSpec, apiUsers);
        }
        if ("testUpdateTagById()".equals(testInfo.getDisplayName()) || "testPostTagAndGetById()".equals(testInfo.getDisplayName())) {
            DeleteData.deleteTagById(checkedId, requestSpec, apiTags);
        }
        if ("testUpdatePostById()".equals(testInfo.getDisplayName()) || "testPostPostAndGetById()".equals(testInfo.getDisplayName())) {
            DeleteData.deletePostById(checkedId, requestSpec, apiPosts);
        }
    }



}
