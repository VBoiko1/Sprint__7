package ru.praktikum.services.scooter;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseCourierTest {

    Gson gson = new Gson();

    @Test
    @DisplayName("Успешная авторизация курьера")
    void successfulAuthorizationTest() {
        //Создание курьера
        CreateСourier courier = new CreateСourier("Avtotester", "qwerty", "Anna");
        Response createResponse = creatCreateСourier(courier);
        verifyResponseStatusCode(createResponse, 201);
        verifyResponseBody(createResponse, true);
        //Авторизация и проверки
        Response authResponse = autorizionCurier(courier.getLogin(), courier.getPassword());
        verifyResponseStatusCode(authResponse,200);
        authResponse.then().assertThat().body("id", notNullValue());
        //Удаляем тестового курьера
        getCourierIdForCleanup(courier.getLogin(), courier.getPassword());

    }
    @Test
    @DisplayName("Проверка авторизации без обязательный  полей")
    void sfdsfsvfd(){
        //Создание курьера
        CreateСourier courier = new CreateСourier("Kamaz","zxc","Bob");
        Response response = creatCreateСourier(courier);
        verifyResponseStatusCode(response,201);
        verifyResponseBody(response,true);
        getCourierIdForCleanup(courier.getLogin(), courier.getPassword());
        //Проверка авторизации без обязательных полей
        checkAuthorizationWithMissingData(null, courier.getPassword());
        checkAuthorizationWithMissingData(courier.getLogin(),null);
        checkAuthorizationWithMissingData(null,null);

    }

    @Test
    @DisplayName("Авторизация несуществующим логином и паролем")
    void authorizationWithNonExistentCredentials(){
        Response response = autorizionCurier("fsfrs","sdfsd");
        verifyResponseStatusCode(response,404);
        verifyErrorResponse(response,404,"Учетная запись не найдена");
    }

    @Step("Авторизация")
    public Response autorizionCurier(String login, String password) {
        AuthorizationCourier auth = new AuthorizationCourier(login, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(gson.toJson(auth))
                .post("/api/v1/courier/login");

        return response;
    }

    @Step("Проверка авторизации с недостающими данными")
    public void checkAuthorizationWithMissingData(String login, String password){
        Response authResponse = autorizionCurier(login,password);
        verifyResponseStatusCode(authResponse,400);
        authResponse.then().assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }

}
