package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
/**
 * Класс тестирования API для взаимодействия с Posts.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Testing WordPressAPI for posts")
public class PostTests extends BaseTest {
    private String title = config.getProperty("post.title");
    private String content = config.getProperty("post.content");
    private String status = config.getProperty("post.status");
    private String apiPosts = config.getProperty("api.posts");
    private String statusNew = config.getProperty("post.status.new");

    /**
     * Тест для создания поста и проверки его ID.
     *
     * Этот тест отправляет POST-запрос для создания поста
     * и проверяет, что статус-код ответа равен 201.
     * ID нового поста сохраняется для дальнейшего использования.
     */
    @Test
    @Order(1)
    @Description("Create a post and verify the ID")
    @Step("Create post with title, content, status")
    public void testCreatePostAndCheckId() {
        Response response = requestSpec
                .formParam("title", title)
                .formParam("content", content)
                .formParam("status", status)
                .when()
                .post(apiPosts);
        response.then().statusCode(201);

        checkedId = response.then()
                .extract()
                .path("id");
    }

    /**
     * Тест для получения поста по его ID.
     *
     * Этот тест отправляет GET-запрос для получения поста
     * по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Order(2)
    @Description("Get post by ID")
    @Step("Get post with ID")
    public void testGetPostById() {
        Response response = requestSpec
                .when()
                .get(apiPosts + checkedId);
        response.then().statusCode(200);
    }

    /**
     * Тест для обновления поста по его ID.
     *
     * Этот тест отправляет POST-запрос для обновления статуса
     * существующего поста по сохраненному ID и проверяет,
     * что статус-код ответа равен 200.
     */
    @Test
    @Order(3)
    @Description("Update post by ID")
    @Step("Update post with ID to new status")
    public void testUpdatePostById() {
        Response response = requestSpec
                .formParam("status", statusNew)
                .when()
                .post(apiPosts + checkedId);
        response.then().statusCode(200);
    }

    /**
     * Тест для удаления поста по его ID.
     *
     * Этот тест отправляет DELETE-запрос для удаления поста
     * по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Order(4)
    @Description("Delete post by ID")
    @Step("Delete post with ID")
    public void testDeletePostById() {
        Response response = requestSpec
                .when()
                .delete(apiPosts + checkedId);
        response.then().statusCode(200);
    }

    /**
     * Тест для создания поста без токена.
     *
     * Этот тест отправляет POST-запрос для создания поста
     * и проверяет, что статус-код ответа равен 401 (Unauthorized).
     */
    @Test
    @Order(5)
    @Description("Try to create post without token")
    @Step("Attempt to create a post with missing authorization token")
    public void testCreatePostWithOutToken() {
        Response response = given()
                .formParam("title", title)
                .formParam("content", content)
                .formParam("status", status)
                .when()
                .post(apiPosts);
        response.then().statusCode(401);
    }

    /**
     * Тест для получения поста по фейковому ID.
     *
     * Этот тест отправляет GET-запрос для получения поста
     * с помощью несуществующего ID и проверяет, что статус-код
     * ответа равен 404 (Not Found).
     */
    @Test
    @Order(6)
    @Description("Get post by fake ID")
    @Step("Attempt to get post with fake ID")
    public void testGetPostByFakeId() {
        Response response = requestSpec
                .when()
                .get(apiPosts + fakeID);
        response.then().statusCode(404);
    }
}
