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
 * Класс тестирования API для взаимодействия с Tags.
 */
@Epic("Testing WordPressAPI for tags")
public class TagTests extends BaseTest {
    private final String name = generator.generateRandomWord(6);
    private final String description = generator.generateRandomWord(9);
    private final String nameNew = generator.generateRandomWord(4);

    /**
     * Инициализирует конфигурационные данные.
     * Выполняется каждый раз перед @test.
     */
    @BeforeEach
    public void setUp() {
        apiTags = config.getProperty("api.tags");
    }

    /**
     * Тест для создания нового тега.
     * Проверяет успешное создание тега и валидирует статус-код 201.
     */
    @Test
    @Description("Create a tag and verify the ID")
    @Step("Create tag with name and description")
    public void testPostTagAndGetById() {
        Response response = requestSpec
                .formParam("description", description)
                .formParam("name", name)
                .when()
                .post(apiTags);
        response.then().statusCode(201);

        checkedId = response.then()
                .extract()
                .path("id");

        Response getTagResponse = requestSpec
                .when()
                .get(apiTags + checkedId);
        getTagResponse.then().statusCode(200);

        getTagResponse.then()
                .body("id", equalTo(checkedId))
                .body("count", notNullValue())
                .body("name", equalTo(name))
                .body("description", equalTo(description))
                .body("taxonomy", equalTo("post_tag"))
                .body("link", containsString("localhost:8000/?tag=" + name.toLowerCase()))
                .body("slug", equalTo(name.toLowerCase()))
                .body("meta", notNullValue())
                .body("_links.self[0].href", containsString("/wp/v2/tags/" + checkedId))
                .body("_links.collection[0].href", containsString("/wp/v2/tags"))
                .body("_links.about[0].href", containsString("/wp/v2/taxonomies/post_tag"))
                .body("_links.\"wp:post_type\"[0].href", containsString("localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fposts&tags=" + checkedId))
                .body("_links.curies", notNullValue());
    }

    /**
     * Тест для создания нового тега с уже использующимся именем.
     * <p>
     * Этот тест отправляет POST-запрос для создания тега
     * и проверяет, что статус-код ответа равен 400.
     */
    @Test
    @Description("Create a tag")
    @Step("Create tag with name was used")
    public void testCreateTagWithUsedName() {
        Response response = requestSpec
                .formParam("description", description)
                .formParam("name", name)
                .when()
                .post(apiTags);
        response.then().statusCode(201);

        Response reResponse = requestSpec
                .formParam("description", description)
                .formParam("name", name)
                .when()
                .post(apiTags);
        reResponse.then().statusCode(400);

        reResponse.then()
                .body("code", equalTo("term_exists"))
                .body("message", equalTo("Элемент с указанным именем и ярлыком уже существует в этой таксономии."))
                .body("data.status", equalTo(400));
    }

    /**
     * Тест для обновления тега по его ID.
     *
     * Этот тест отправляет POST-запрос для обновления данных
     * тега по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Description("Update tag by ID")
    @Step("Update tag with ID")
    public void testUpdateTagById() {
        Response response = requestSpec
                .formParam("description", description)
                .formParam("name", name)
                .when()
                .post(apiTags);
        response.then().statusCode(201);

        checkedId = response.then()
                .extract()
                .path("id");

        Response postTagResponse = requestSpec
                .formParam("name", nameNew)
                .when()
                .post(apiTags + checkedId);
        postTagResponse.then().statusCode(200);

        postTagResponse.then()
                .body("name", equalTo(nameNew));

        postTagResponse.then()
                .body("id", equalTo(checkedId))
                .body("count", notNullValue())
                .body("description", equalTo(description))
                .body("taxonomy", equalTo("post_tag"))
                .body("link", containsString("localhost:8000/?tag=" + name.toLowerCase()))
                .body("slug", equalTo(name.toLowerCase()))
                .body("meta", notNullValue())
                .body("_links.self[0].href", containsString("/wp/v2/tags/" + checkedId))
                .body("_links.collection[0].href", containsString("/wp/v2/tags"))
                .body("_links.about[0].href", containsString("/wp/v2/taxonomies/post_tag"))
                .body("_links.\"wp:post_type\"[0].href", containsString("localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fposts&tags=" + checkedId))
                .body("_links.curies", notNullValue());
    }

    /**
     * Тест для удаления тега по его ID.
     *
     * Этот тест отправляет DELETE-запрос для удаления тега
     * по сохраненному ID и проверяет, что статус-код ответа равен 200.
     */
    @Test
    @Description("Delete tag by ID")
    @Step("Delete tag with ID")
    public void testDeleteTagById() {
        Response response = requestSpec
                .formParam("description", description)
                .formParam("name", name)
                .when()
                .post(apiTags);
        response.then().statusCode(201);

        checkedId = response.then()
                .extract()
                .path("id");

        Response deleteTagResponse = requestSpec
                .queryParam("force", true)
                .when()
                .delete(apiTags + checkedId);
        deleteTagResponse.then().statusCode(200);

        deleteTagResponse.then()
                .body("deleted", equalTo(true))
                .body("previous.id", equalTo(checkedId))
                .body("previous.count", notNullValue())
                .body("previous.description", notNullValue())
                .body("previous.taxonomy", equalTo("post_tag"))
                .body("previous.link", containsString("localhost:8000/?tag=" + name.toLowerCase()))
                .body("previous.slug", equalTo(name.toLowerCase()))
                .body("previous.meta", notNullValue());
    }

    /**
     * Тест для создания тега без токена.
     *
     * Этот тест отправляет POST-запрос для создания тега
     * и проверяет, что статус-код ответа равен 401 (Unauthorized).
     */
    @Test
    @Description("Try to create tag without token")
    @Step("Attempt to create a tag with a missing authorization token")
    public void testCreateTagWithOutToken() {
        Response response = given()
                .formParam("name", name)
                .formParam("description", description)
                .when()
                .post(apiTags);
        response.then().statusCode(401);
        response.then()
                .body("code", equalTo("rest_cannot_create"))
                .body("message", equalTo("Извините, вам не разрешено создавать элементы этой таксономии."))
                .body("data.status", equalTo(401));
    }

    /**
     * Тест для получения тега по фейковому ID.
     *
     * Этот тест отправляет GET-запрос для получения тега
     * с помощью несуществующего ID и проверяет, что статус-код
     * ответа равен 404 (Not Found).
     */
    @Test
    @Description("Get tag by fake ID")
    @Step("Attempt to get tag with fake ID")
    public void testGetTagByFakeId() {
        Response response = requestSpec
                .when()
                .get(apiTags + fakeID);
        response.then().statusCode(404);

        response.then()
                .body("code", equalTo("rest_term_invalid"))
                .body("message", equalTo("Элемент не существует."))
                .body("data.status", equalTo(404));
    }
}