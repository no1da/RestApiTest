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
 * Класс тестирования API для взаимодействия с Users.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Testing WordPressAPI for users")
public class UserTests extends BaseTest {
    private String username = config.getProperty("user.username");
    private String email = config.getProperty("user.email");
    private String password = config.getProperty("user.password");
    private String descriptionUpdated = config.getProperty("user.description.updated");
    private String description = config.getProperty("user.description");
    private String apiUsers = config.getProperty("api.users");
    private String reassign = "&reassign=1";

    /**
     * Тест для создания нового пользователя.
     * Проверяет успешное создание пользователя и валидирует статус-код 201.
     */
    @Test
    @Order(1)
    @Description("Create a new user and verify the ID")
    @Step("Create user with username, email, and password")
    public void testCreateUserAndCheckId() {
        Response response = requestSpec
                .formParam("username", username)
                .formParam("email", email)
                .formParam("password", password)
                .formParam("description", description)
                .when()
                .post(apiUsers);
        response.then().statusCode(201);

        checkedId = response.then()
                .extract()
                .path("id");
    }

    /**
     * Тест для получения пользователя по его ID.
     * Проверяет, что пользователь успешно возвращён с кодом 200.
     */
    @Test
    @Order(2)
    @Description("Get user by ID")
    @Step("Get user using the created ID")
    public void testGetUserById() {
        Response response = requestSpec
                .when()
                .get(apiUsers + checkedId);
        response.then().statusCode(200);
    }

    /**
     * Тест для обновления пользователя по его ID.
     * Проверяет, что обновление выполнено успешно и возвращён код 200.
     */
    @Test
    @Order(3)
    @Description("Update user by ID")
    @Step("Update user with new username")
    public void testUpdateUserById() {
        Response response = requestSpec
                .formParam("description", descriptionUpdated)
                .when()
                .post(apiUsers + checkedId);
        response.then().statusCode(200);
    }

    /**
     * Тест для удаления пользователя по его ID.
     * Проверяет, что пользователь удалён успешно с кодом 200.
     */
    @Test
    @Order(4)
    @Description("Delete user by ID")
    @Step("Delete user with created ID")
    public void testDeleteUserById() {
        Response response = requestSpec
                .when()
                .delete(apiUsers + checkedId + forceDelete + reassign);
        response.then().statusCode(200);
    }

    /**
     * Тест для получения текущего пользователя ("me").
     * Проверяет, что текущий пользователь возвращён успешно с кодом 200.
     */
    @Test
    @Order(5)
    @Description("Get current user (me)")
    @Step("Retrieve details of the currently logged-in user")
    public void testGetCurrentUser() {
        Response response = requestSpec
                .when()
                .get(apiUsers + "me");
        response.then().statusCode(200);
    }

    /**
     * Тест для обновления текущего пользователя ("me").
     * Проверяет успешность обновления и возвращение кода 200.
     */
    @Test
    @Order(6)
    @Description("Update current user (me)")
    @Step("Update the details of the currently logged-in user")
    public void testUpdateCurrentUser() {
        Response response = requestSpec
                .formParam("description", descriptionUpdated)
                .when()
                .post(apiUsers + "me");
        response.then().statusCode(200);
    }

    /**
     * Тест создания пользователя с некорректным токеном.
     * Проверяет, что система возвращает HTTP-код 401 (Unauthorized).
     */
    @Test
    @Order(7)
    @Description("Try to create user with invalid token")
    @Step("Attempt creating a user with missing/invalid authorization token")
    public void testCreateUserWithInvalidToken() {
        Response response = given()
                .formParam("username", username)
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post(apiUsers);
        response.then().statusCode(401);
    }

    /**
     * Тест получения пользователя с некорректным ID.
     * Возвращает статус-код 404 (Not Found).
     */
    @Test
    @Order(8)
    @Description("Get user by fake ID")
    @Step("Attempt to get user with non-existent ID")
    public void testGetUserWithInvalidId() {
        Response response = requestSpec
                .when()
                .get(apiUsers + fakeID);
        response.then().statusCode(404);
    }
}