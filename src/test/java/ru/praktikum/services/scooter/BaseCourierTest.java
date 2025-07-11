package ru.praktikum.services.scooter;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BaseCourierTest {
    private Gson gson = new Gson();
    private Integer courierIdToCleanup = null;  // Поле для хранения ID курьера, которого нужно удалить

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @AfterEach
    public void tearDown() {
        if (courierIdToCleanup != null) {
            deleteCourier(courierIdToCleanup);
            courierIdToCleanup = null;
        }
    }
    @Step("Создание курьера")
    public Response createCreateCourier(CreateСourier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        }



    @Step("Удаление курьера")
    public void deleteCourier(int courierId) {
        given()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Step("Получение ID курьера")
    public int getCourierId(String login, String password) {
        AuthorizationCourier auth = new AuthorizationCourier(login, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(gson.toJson(auth))
                .post("/api/v1/courier/login");

        return response.jsonPath().getInt("id");
    }
    @Step("Проверка кода ответа")
    public void verifyResponseStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }
    @Step("Проверка тела успешного ответа")
    public void verifyResponseBody(Response response, boolean responseBody) {
        response.then().assertThat().body("ok", equalTo(responseBody));
    }
    @Step("Проверка тела ответа с ошибкой")
    public void verifyErrorResponse(Response response, int expectedCode,
                                    String expectedMessage) {
        response.then()
                .assertThat()
                .body("code", equalTo(expectedCode))
                .body("message", equalTo(expectedMessage));
    }
    @Step("Авторизация для получения Id")
    public int authorizeAndGetId(String login, String password) {
        AuthorizationCourier authCourier = new AuthorizationCourier(login, password);
        String requestBody = gson.toJson(authCourier);

        Response response = given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .post("/api/v1/courier/login");

        response.then()
                .statusCode(200)
                .body("id", notNullValue());

        String responseBody = response.getBody().asString();
        AuthorizationCourierResponse authResponse = gson.fromJson(responseBody, AuthorizationCourierResponse.class);
        return authResponse.getId();
    }
    @Step("Получение ID курьера для последующего удаления")
    public int getCourierIdForCleanup(String login, String password) {
        int courierId = authorizeAndGetId(login, password);
        courierIdToCleanup = courierId;
        return courierId;
    }


}
