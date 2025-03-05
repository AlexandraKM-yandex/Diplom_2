package order;


import base.HttpClient;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.Constants.*;
import static io.restassured.RestAssured.given;

public class OrderSteps extends HttpClient {

    @Step("Получение данных об ингредиентах")
    public ValidatableResponse getIngredientsData() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(INGREDIENTS_LIST)
                .then();
    }

    @Step("Получение хэшей ингредиентов")
    public ArrayList<String> getIngredientsIds() {
        return getIngredientsData().extract().path("data._id");
    }


    @Step("Создание заказа после авторизации")
    public ValidatableResponse orderCreateAfterAuthorization(List<String> ingredients, String accessToken) {

        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredients);

        return given()
                .spec(requestSpecification())
                .and()
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post(ORDER_CREATE_POST)
                .then();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse orderCreateWithoutAuthorization(List<String> ingredients) {

        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredients);

        return given()
                .spec(requestSpecification())
                .and()
                .body(body)
                .when()
                .post(ORDER_CREATE_POST)
                .then();
    }

    @Step("Получение заказов конкретного пользователя после авторизации")
    public ValidatableResponse getOrdersAfterAuthorization(String accessToken) {

        return given()
                .spec(requestSpecification())
                .and()
                .header("Authorization", accessToken)
                .when()
                .get(USERS_ORDER_GET)
                .then();
    }

    @Step("Получение заказов конкретного пользователя без авторизации")
    public ValidatableResponse getOrdersWithoutAuthorization() {

        return given()
                .spec(requestSpecification())
                .when()
                .get(USERS_ORDER_GET)
                .then();
    }

}