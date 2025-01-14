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
 * Класс тестирования API для взаимодействия с Users.
 */
@Epic("Testing WordPressAPI for users")
public class UserTests extends BaseTest {
    private String username = generator.generateRandomWord(5);
    private String email = generator.generateRandomEmail();
    private String password = generator.generateRandomWord(11);
    private String descriptionUpdated = generator.generateRandomWord(7);
    private String description = generator.generateRandomWord(8);

    /**
     * Инициализирует конфигурационные данные.
     * Выполняется каждый раз перед @test.
     */
    @BeforeEach
    public void setUp() {
        apiUsers = config.getProperty("api.users");
    }

    /**
     * Тест для создания нового пользователя.
     * Проверяет успешное создание пользователя и валидирует статус-код 201.
     */
    @Test
    @Description("Create a new user and verify the ID")
    @Step("Create user with username, email, and password")
    public void testPostUserAndGetById() {
        Response postUserResponse = requestSpec
                .formParam("username", username)
                .formParam("email", email)
                .formParam("password", password)
                .formParam("description", description)
                .when()
                .post(apiUsers);
        postUserResponse.then().statusCode(201);

        checkedId = postUserResponse.then().extract().path("id");

        Response getUserResponse = requestSpec
                .when()
                .get(apiUsers + checkedId);
        getUserResponse.then().statusCode(200);

        getUserResponse.then()
                .body("id", equalTo(checkedId))
                .body("name", equalTo(username))
                .body("description", equalTo(description))
                .body("url", notNullValue())
                .body("link", containsString("localhost:8000/?author=" + checkedId))
                .body("slug", equalTo(username.toLowerCase()))
                .body("avatar_urls.24", containsString("secure.gravatar.com/avatar"))
                .body("avatar_urls.48", containsString("secure.gravatar.com/avatar"))
                .body("avatar_urls.96", containsString("secure.gravatar.com/avatar"))
                .body("_links.self[0].href", containsString("/wp/v2/users/" + checkedId))
                .body("_links.collection[0].href", containsString("/wp/v2/users"));
    }

    /**
     * Тест для обновления пользователя по его ID.
     * Проверяет, что обновление выполнено успешно и возвращён код 200.
     */
    @Test
    @Description("Update user by ID")
    @Step("Update user with new username")
    public void testUpdateUserById() {
        Response postUserResponse = requestSpec
                .formParam("username", username)
                .formParam("email", email)
                .formParam("password", password)
                .formParam("description", description)
                .when()
                .post(apiUsers);
        postUserResponse.then().statusCode(201);

        checkedId = postUserResponse.then().extract().path("id");

        Response updateUserResponse = requestSpec
                .formParam("description", descriptionUpdated)
                .when()
                .post(apiUsers + checkedId);
        updateUserResponse.then().statusCode(200);

        updateUserResponse.then()
                .body("description", equalTo(descriptionUpdated));

        updateUserResponse.then()
                .body("id", equalTo(checkedId))
                .body("username", equalTo(username))
                .body("name", equalTo(username))
                .body("first_name", notNullValue())
                .body("last_name", notNullValue())
                .body("link", containsString("http://localhost:8000/?author=" + checkedId))
                .body("email", equalTo(email))
                .body("locale", containsString("ru_RU"))
                .body("nickname", equalTo(username))
                .body("roles[0]", equalTo("subscriber"))
                .body("registered_date", notNullValue())
                .body("roles", hasItem("subscriber"))
                .body("_links.self[0].href", containsString("/wp/v2/users/" + checkedId))
                .body("_links.collection[0].href", containsString("/wp/v2/users"))
                .body("capabilities.read", equalTo(true))
                .body("capabilities.level_0", equalTo(true))
                .body("capabilities.subscriber", equalTo(true))
                .body("extra_capabilities.subscriber", equalTo(true))
                .body("avatar_urls.96", containsString("secure.gravatar.com"));
    }

    /**
     * Тест для удаления пользователя по его ID.
     * Проверяет, что пользователь удалён успешно с кодом 200.
     */
    @Test
    @Description("Delete user by ID")
    @Step("Delete user with created ID")
    public void testDeleteUserById() {
        Response postUserResponse = requestSpec
                .formParam("username", username)
                .formParam("email", email)
                .formParam("password", password)
                .formParam("description", description)
                .when()
                .post(apiUsers);
        postUserResponse.then().statusCode(201);

        checkedId = postUserResponse.then().extract().path("id");

        Response deleteResponse = requestSpec
                .queryParam("force", true)
                .queryParam("reassign", 1)
                .when()
                .delete(apiUsers + checkedId);
        deleteResponse.then().statusCode(200);

        deleteResponse.then()
                .body("deleted", equalTo(true))
                .body("previous.id", equalTo(checkedId))
                .body("previous.username", equalTo(username))
                .body("previous.name", equalTo(username))
                .body("previous.description", equalTo(description))
                .body("previous.url", notNullValue())
                .body("previous.link", containsString("localhost:8000/?author=" + checkedId))
                .body("previous.slug", equalTo(username.toLowerCase()))
                .body("previous.roles", hasItem("subscriber"))
                .body("previous.registered_date", notNullValue())
                .body("previous.capabilities.read", equalTo(true))
                .body("previous.capabilities.level_0", equalTo(true))
                .body("previous.capabilities.subscriber", equalTo(true))
                .body("previous.extra_capabilities.subscriber", equalTo(true))
                .body("previous.avatar_urls.24", containsString("secure.gravatar.com/avatar"))
                .body("previous.avatar_urls.48", containsString("secure.gravatar.com/avatar"))
                .body("previous.avatar_urls.96", containsString("secure.gravatar.com/avatar"));
    }

    /**
     * Тест для получения текущего пользователя ("me").
     * Проверяет, что текущий пользователь возвращён успешно с кодом 200.
     */
    @Test
    @Description("Get current user (me)")
    @Step("Retrieve details of the currently logged-in user")
    public void testGetCurrentUser() {
        checkedId = 1;
        username = "Firstname.LastName";

        Response response = requestSpec
                .when()
                .get(apiUsers + "me");
        response.then().statusCode(200);

        response.then()
                .body("id", equalTo(checkedId))
                .body("name", equalTo(username))
                .body("description", notNullValue())
                .body("url", notNullValue())
                .body("link", containsString("http://localhost:8000/?author=" + checkedId))
                .body("slug", equalTo(username.toLowerCase()))
                .body("avatar_urls.24", containsString("secure.gravatar.com/avatar"))
                .body("avatar_urls.48", containsString("secure.gravatar.com/avatar"))
                .body("avatar_urls.96", containsString("secure.gravatar.com/avatar"))
                .body("_links.self[0].href", containsString("/wp/v2/users/" + checkedId))
                .body("_links.collection[0].href", containsString("/wp/v2/users"));
    }

    /**
     * Тест для обновления текущего пользователя ("me").
     * Проверяет успешность обновления и возвращение кода 200.
     */
    @Test
    @Description("Update current user (me)")
    @Step("Update the details of the currently logged-in user")
    public void testUpdateCurrentUser() {
        checkedId = 1;
        username = "Firstname.LastName";
        email = "firstname.lastname@simbirsoft.com";

        Response response = requestSpec
                .formParam("description", descriptionUpdated)
                .when()
                .post(apiUsers + "me");
        response.then().statusCode(200);

        response.then()
                .body("description", equalTo(descriptionUpdated));

        response.then()
                .body("id", equalTo(checkedId))
                .body("username", equalTo(username))
                .body("name", equalTo(username))
                .body("first_name", notNullValue())
                .body("last_name", notNullValue())
                .body("link", containsString("http://localhost:8000/?author=" + checkedId))
                .body("email", equalTo(email))
                .body("locale", containsString("ru_RU"))
                .body("nickname", equalTo(username))
                .body("roles[0]", equalTo("administrator"))
                .body("registered_date", notNullValue())
                .body("_links.self[0].href", containsString("/wp/v2/users/" + checkedId))
                .body("_links.collection[0].href", containsString("/wp/v2/users"))
                .body("capabilities", notNullValue())
                .body("meta", notNullValue())
                .body("avatar_urls", notNullValue());
    }

    /**
     * Тест создания пользователя с некорректным токеном.
     * Проверяет, что система возвращает HTTP-код 401 (Unauthorized).
     */
    @Test
    @Description("Try to create user with invalid token")
    @Step("Attempt creating a user with missing/invalid authorization token")
    public void testCreateUserWithOutToken() {
        Response response = given()
                .formParam("username", username)
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post(apiUsers);
        response.then().statusCode(401);
        response.then()
                .body("code", equalTo("rest_cannot_create_user"))
                .body("message", equalTo("Извините, вам не разрешено создавать новых пользователей."))
                .body("data.status", equalTo(401));
    }

    /**
     * Тест получения пользователя с некорректным ID.
     * Возвращает статус-код 404 (Not Found).
     */
    @Test
    @Description("Get user by fake ID")
    @Step("Attempt to get user with non-existent ID")
    public void testGetUserWithInvalidId() {
        Response response = requestSpec
                .when()
                .get(apiUsers + fakeID);
        response.then().statusCode(404);
        response.then()
                .body("code", equalTo("rest_user_invalid_id"))
                .body("message", equalTo("Неверный ID пользователя."))
                .body("data.status", equalTo(404));
    }
}