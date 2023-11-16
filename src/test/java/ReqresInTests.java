import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class ReqresInTests extends TestBase {
    @Test
    void checkUserDataTest() {
        given()
                .log().uri()
                .log().method()
                .when()
                .get("/users/5")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.id", equalTo(5),
                        "data.email", equalTo("charles.morris@reqres.in"),
                        "data.first_name", equalTo("Charles"),
                        "data.last_name", equalTo("Morris")
                );
    }

    @Test
    void getNotExistentUserTest() {
        given()
                .when()
                .get("/users/23")
                .then()
                .statusCode(404);
    }

    @Test
    void createUserTest() {
        given()
                .body("{\"name\": \"morpheus\",\"job\": \"leader\"}")
                .contentType(JSON)
                .when()
                .post("/users")
                .then()
                .statusCode(201);
    }

    @Test
    void deleteUserTest() {
        given()
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204);
    }

    @Test
    void checkSuccessfulRegisterTest() {
        given()
                .body("{ \"email\": \"eve.holt@reqres.in\",\"password\": \"pistol\"}")
                .contentType(JSON)
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("id", is(4),
                        "token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void checkUnsuccessfulRegisterTest() {
        given()
                .body("{\"email\": \"sydney@fife\"}")
                .contentType(JSON)
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
}
