package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
/**
 * Класс тестирования API для взаимодействия с Tags.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Testing WordPressAPI for tags")
public class TagTests extends BaseTest {
    private final String name = config.getProperty("tag.name");
    private final String description = config.getProperty("tag.description");
    private final String apiTags = config.getProperty("api.tags");
    private final String nameNew = config.getProperty("tag.name.new");

    /**
     * Тест для создания нового тега.
     *
     * Этот тест отправляет POST-запрос для создания тега
     * и проверяет, что статус-код ответа равен 201.
     * ID нового тега сохраняется для последующего использования.
     */
    @Test
    @Order(1)
    @Description("Create a tag and verify the ID")
    @Step("Create tag with name and description")
    public void testCreateTagAndCheckId() {
        Response response = requestSpec
                .formParam("description", description)
                .formParam("name", name)
                .when()
                .post(apiTags);
        response.then().statusCode(201);

        checkedId = response.then()
                .extract()
                .path("id");
    }

    @Test
    @Order(2)
    @Description("Create a tag")
    @Step("Create tag with name was used")
    public void testCreateTagWithUsedName() {
        Response response = requestSpec
                .formParam("description", description)
                .formParam("name", name)
                .when()
                .post(apiTags);
        response.then().statusCode(400);
    }

    /**
     * Тест для получения тега по его ID.
     *
     * Этот тест отправляет GET-запрос для получения тега
     * по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Order(3)
    @Description("Get tag by ID")
    @Step("Get tag with ID")
    public void testGetTagById() {
        Response response = requestSpec
                .when()
                .get(apiTags + checkedId);
        response.then().statusCode(200);
    }

    /**
     * Тест для обновления тега по его ID.
     *
     * Этот тест отправляет POST-запрос для обновления данных
     * тега по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Order(4)
    @Description("Update tag by ID")
    @Step("Update tag with ID")
    public void testUpdateTagById() {
        Response response = requestSpec
                .formParam("name", nameNew)
                .when()
                .post(apiTags + checkedId);
        response.then().statusCode(200);
    }

    /**
     * Тест для удаления тега по его ID.
     *
     * Этот тест отправляет DELETE-запрос для удаления тега
     * по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Order(5)
    @Description("Delete tag by ID")
    @Step("Delete tag with ID")
    public void testDeleteTagById() {
        Response response = requestSpec
                .when()
                .delete(apiTags + checkedId + forceDelete);
        response.then().statusCode(200);
    }

    /**
     * Тест для создания тега без токена.
     *
     * Этот тест отправляет POST-запрос для создания тега
     * и проверяет, что статус-код ответа равен 401 (Unauthorized).
     */
    @Test
    @Order(6)
    @Description("Try to create tag without token")
    @Step("Attempt to create a tag with a missing authorization token")
    public void testCreateTagWithOutToken() {
        Response response = given()
                .formParam("name", name)
                .formParam("description", description)
                .when()
                .post(apiTags);
        response.then().statusCode(401);
    }

    /**
     * Тест для получения тега по фейковому ID.
     *
     * Этот тест отправляет GET-запрос для получения тега
     * с помощью несуществующего ID и проверяет, что статус-код
     * ответа равен 404 (Not Found).
     */
    @Test
    @Order(7)
    @Description("Get tag by fake ID")
    @Step("Attempt to get tag with fake ID")
    public void testGetTagByFakeId() {
        Response response = requestSpec
                .when()
                .get(apiTags + fakeID);
        response.then().statusCode(404);
    }
}