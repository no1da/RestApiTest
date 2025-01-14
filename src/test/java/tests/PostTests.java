package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Класс тестирования API для взаимодействия с Posts.
 */
@Epic("Testing WordPressAPI for posts")
public class PostTests extends BaseTest {
    private String title = generator.generateRandomWord(12);
    private String content = generator.generateRandomWord(14);
    private String status = config.getProperty("post.status");
    private String statusNew = config.getProperty("post.status.new");

    /**
     * Инициализирует конфигурационные данные.
     * Выполняется каждый раз перед @test.
     */
    @BeforeEach
    public void setUp() {
        apiPosts = config.getProperty("api.posts");
    }
    /**
     * Тест для создания поста и получения по его ID.
     * Проверяет успешное создание поста и валидирует статус-код 201.
     */
    @Test
    @Description("Create a post and verify the ID")
    @Step("Create post with title, content, status")
    public void testPostPostAndGetById() {
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

        Response getPostResponse = requestSpec
                .when()
                .get(apiPosts + checkedId);
        getPostResponse.then().statusCode(200);

        getPostResponse.then()
                .body("id", equalTo(checkedId))
                .body("date", notNullValue())
                .body("date_gmt", notNullValue())
                .body("guid.rendered", equalTo("http://localhost:8000/?p=" + checkedId))
                .body("modified", notNullValue())
                .body("modified_gmt", notNullValue())
                .body("status", equalTo(status))
                .body("type", equalTo("post"))
                .body("title.rendered", equalTo(title))
                .body("link", equalTo("http://localhost:8000/?p=" + checkedId))
                .body("content.rendered", containsString(content))
                .body("author", notNullValue())
                .body("comment_status", equalTo("open"))
                .body("ping_status", equalTo("open"))
                .body("sticky", equalTo(false))
                .body("template", equalTo(""))
                .body("format", equalTo("standard"))
                .body("meta", notNullValue())
                .body("categories", hasSize(1))
                .body("tags", empty())
                .body("class_list", hasItem("post-" + checkedId))
                .body("_links", notNullValue());
    }

    /**
     * Тест для обновления поста по его ID.
     *
     * Этот тест отправляет POST-запрос для обновления статуса
     * существующего поста по сохраненному ID и проверяет,
     * что статус-код ответа равен 200.
     */
    @Test
    @Description("Update post by ID")
    @Step("Update post with ID to new status")
    public void testUpdatePostById() {
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

        Response updatePostResponse = requestSpec
                .formParam("status", statusNew)
                .when()
                .post(apiPosts + checkedId);
        updatePostResponse.then().statusCode(200);

        updatePostResponse.then()
                .body("id", equalTo(checkedId))
                .body("date", notNullValue())
                .body("date_gmt", notNullValue())
                .body("guid.rendered", equalTo("http://localhost:8000/?p=" + checkedId))
                .body("modified", notNullValue())
                .body("modified_gmt", notNullValue())
                .body("status", equalTo(statusNew))
                .body("type", equalTo("post"))
                .body("title.rendered", equalTo(title))
                .body("title.raw", equalTo(title))
                .body("link", equalTo("http://localhost:8000/?p=" + checkedId))
                .body("content.rendered", containsString(content))
                .body("content.raw", containsString(content))
                .body("author", notNullValue())
                .body("featured_media", notNullValue())
                .body("comment_status", equalTo("open"))
                .body("ping_status", equalTo("open"))
                .body("sticky", equalTo(false))
                .body("template", equalTo(""))
                .body("format", equalTo("standard"))
                .body("meta", notNullValue())
                .body("categories", hasSize(1))
                .body("tags", empty())
                .body("class_list", hasItem("post-" + checkedId))
                .body("_links", notNullValue());
    }

    /**
     * Тест для удаления поста по его ID.
     *
     * Этот тест отправляет DELETE-запрос для удаления поста
     * по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Description("Delete post by ID")
    @Step("Delete post with ID")
    public void testDeletePostById() {
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

        Response deletePostResponse = requestSpec
                .when()
                .delete(apiPosts + checkedId);
        deletePostResponse.then().statusCode(200);

        deletePostResponse.then()
                .body("id", equalTo(checkedId))
                .body("date", notNullValue())
                .body("date_gmt", notNullValue())
                .body("guid.rendered", equalTo("http://localhost:8000/?p=" + checkedId))
                .body("modified", notNullValue())
                .body("modified_gmt", notNullValue())
                .body("status", equalTo("trash"))
                .body("type", equalTo("post"))
                .body("title.rendered", equalTo(title))
                .body("title.raw", equalTo(title))
                .body("link", equalTo("http://localhost:8000/?p=" + checkedId))
                .body("content.rendered", containsString(content))
                .body("content.raw", containsString(content))
                .body("author", notNullValue())
                .body("featured_media", notNullValue())
                .body("comment_status", equalTo("open"))
                .body("ping_status", equalTo("open"))
                .body("sticky", equalTo(false))
                .body("template", equalTo(""))
                .body("format", equalTo("standard"))
                .body("meta", notNullValue())
                .body("categories", hasSize(1))
                .body("tags", empty())
                .body("class_list", hasItem("post-" + checkedId))
                .body("_links", notNullValue());
    }

    /**
     * Тест для создания поста без токена.
     *
     * Этот тест отправляет POST-запрос для создания поста
     * и проверяет, что статус-код ответа равен 401 (Unauthorized).
     */
    @Test
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

        response.then()
                .body("code", equalTo("rest_cannot_create"))
                .body("message", equalTo("Извините, вам не разрешено создавать записи от лица этого пользователя."))
                .body("data.status", equalTo(401));
    }

    /**
     * Тест для получения поста по фейковому ID.
     *
     * Этот тест отправляет GET-запрос для получения поста
     * с помощью несуществующего ID и проверяет, что статус-код
     * ответа равен 404 (Not Found).
     */
    @Test
    @Description("Get post by fake ID")
    @Step("Attempt to get post with fake ID")
    public void testGetPostByFakeId() {
        Response response = requestSpec
                .when()
                .get(apiPosts + fakeID);
        response.then().statusCode(404);

        response.then()
                .body("code", equalTo("rest_post_invalid_id"))
                .body("message", equalTo("Неверный ID записи."))
                .body("data.status", equalTo(404));
    }
}
