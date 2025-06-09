package ru.praktikum.services.scooter;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ParametrazeCreateOrderTest {
    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @ParameterizedTest
    @MethodSource("orderParameters")
    @DisplayName("Параметризованный тест создания заказа")
    public void createOrder(String firstName,
                            String lastName,
                            String address,
                            String metroStation,
                            String phone,
                            int rentTime,
                            String deliveryDate,
                            String comment,
                            String[] color) {
        CreateOrder order = new CreateOrder(firstName,
                lastName,
                address,
                metroStation,
                phone,
                rentTime,
                deliveryDate,
                comment,
                color);
        Response response = createOrder(order);
        verifyOrderResponse(response);


    }

    static Stream<Arguments> orderParameters() {
        return Stream.of(
                Arguments.of("Иван",
                        "Иванов",
                        "ул. Ленина, 1",
                        "4",
                        "+79991112233",
                        3,
                        "2023-12-31",
                        "Комментарий",
                        new String[]{"BLACK"}
                ),
                Arguments.of("Петр",
                        "Петров",
                        "ул. Пушкина, 10",
                        "5",
                        "+79994445566",
                        5,
                        "2023-11-30",
                        "",
                        new String[]{"GREY"}
                ),
                Arguments.of("Сергей",
                        "Сергеев",
                        "ул. Гагарина, 42",
                        "10",
                        "+79997778899",
                        1,
                        "2023-10-15",
                        "Срочный заказ",
                        new String[]{"BLACK", "GREY"}
                ),
                Arguments.of( "Анна",
                        "Смирнова",
                        "пр. Мира, 15",
                        "7",
                        "+79993334455",
                        2,
                        "2023-09-20",
                        null,
                        new String[]{})

        );
    }

    @Step("Выполнить запрос на создание заказа")

    public Response createOrder(CreateOrder order) {
        return given()
                .header("Content-type", "application/json")
                .body(gson.toJson(order))
                .post("/api/v1/orders");
    }

    @Step("Проверка ответа на создание заказа")
    private void verifyOrderResponse(Response response) {

        response.then().statusCode(201);


        int trackNumber = response.then().extract().path("track");
        assertThat(trackNumber, notNullValue());
    }


}
