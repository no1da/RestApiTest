package utils;

import io.restassured.specification.RequestSpecification;

public class DeleteDataUtils {
    public static void deleteUserById(int checkedId, RequestSpecification requestSpec, String apiUsers) {
        requestSpec
                .when()
                .queryParam("force", true)
                .queryParam("reassign", 1)
                .delete(apiUsers + checkedId)
                .then()
                .statusCode(200);
    }

    public static void deleteTagById(int checkedId, RequestSpecification requestSpec, String apiTags) {
        requestSpec
                .queryParam("force", true)
                .when()
                .delete(apiTags + checkedId)
                .then()
                .statusCode(200);
    }

    public static void deletePostById(int checkedId, RequestSpecification requestSpec, String apiPosts) {
        requestSpec
                .when()
                .delete(apiPosts + checkedId)
                .then()
                .statusCode(200);
    }
}
