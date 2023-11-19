package tests;

import models.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.UserSpec.requestSpec;
import static specs.UserSpec.responseSpec;

public class ReqresInTests extends TestBase {
    @Test
    void checkUserDataTest() {
        step("Получение информации о существующем пользователе", () -> {
            given(requestSpec)
                    .get("/users/5")
                    .then()
                    .spec(responseSpec)
                    .statusCode(200);
        });
    }

    @Test
    void getNotExistentUserTest() {
        step("Получение информации о несуществующем пользователе", () -> {
            given(requestSpec)
                    .get("/users/23")
                    .then()
                    .spec(responseSpec)
                    .statusCode(404);
        });
    }

    @Test
    void createUserTest() {
        CreateUserDataModel userBody = new CreateUserDataModel();
        userBody.setName("morpheus");
        userBody.setJob("leader");

        CreateUserResponseModel response = step("Отправка запроса на создание пользователя", () ->
                given(requestSpec)
                        .body(userBody)
                        .when()
                        .post("/users")
                        .then()
                        .spec(responseSpec)
                        .statusCode(201)
                        .extract().as(CreateUserResponseModel.class)
        );
        step("Проверка данных из ответа", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("leader", response.getJob());
        });
    }

    @Test
    void deleteUserTest() {
        step("Удаление пользователя", () -> {
            given(requestSpec)
                    .delete("/users/2")
                    .then()
                    .spec(responseSpec)
                    .statusCode(204);
        });
    }

    @Test
    void checkSuccessfulRegisterTest() {
        SuccessfulRegisterRequestModel authBody = new SuccessfulRegisterRequestModel();
        authBody.setEmail("eve.holt@reqres.in");
        authBody.setPassword("pistol");
        SuccessfulRegisterResponseModel response = step("Отправка запроса на успешную регистрацию", () ->
                given(requestSpec)
                        .body(authBody)
                        .when()
                        .post("/register")
                        .then()
                        .spec(responseSpec)
                        .statusCode(200)
                        .extract().as(SuccessfulRegisterResponseModel.class)
        );
        step("Проверка идентификатора и токена из ответа", () -> {
            assertEquals("4", response.getId());
            assertEquals("QpwL5tke4Pnpja7X4", response.getToken());
        });
    }

    @Test
    void checkUsersDataOnPageTest() {
        UserListResponseModel response = step("Запрос списка пользователей", () ->
                given(requestSpec)
                        .when()
                        .get("users?page=2")
                        .then()
                        .spec(responseSpec)
                        .statusCode(200)
                        .extract().as(UserListResponseModel.class));
        step("Проверка данных из ответа", () -> {
            assertEquals(2, response.getPage());
            assertEquals(6, response.getPerPage());
            assertEquals(12,response.getTotal());
            assertEquals(2, response.getTotalPages());
        });
        step("Проверка данных о первом объекте из ключа data в ответе", () -> {
            List<UserListResponseModel.DataList> data = response.getData();
            assertEquals(7, data.get(0).getId());
            assertEquals("michael.lawson@reqres.in", data.get(0).getEmail());
            assertEquals("Michael", data.get(0).getFirstName());
            assertEquals("Lawson", data.get(0).getLastName());
            assertEquals("https://reqres.in/img/faces/7-image.jpg", data.get(0).getAvatar());
        });
    }


    @Test
    void checkUnsuccessfulRegisterTest() {
        UnsuccessfulRegisterRequestModel authBody = new UnsuccessfulRegisterRequestModel();
        authBody.setEmail("sydney@fife");
        UnsuccessfulRegisterResponseModel response = step("Отправка запроса на неудачную регистрацию", () ->
                given(requestSpec)
                        .body(authBody)
                        .when()
                        .post("/register")
                        .then()
                        .spec(responseSpec)
                        .statusCode(400)
                        .extract().as(UnsuccessfulRegisterResponseModel.class)
        );
        step("Проверка сообщения об отсутствии пароля из ответа", () -> {
            assertEquals("Missing password", response.getError());
        });
    }
}
