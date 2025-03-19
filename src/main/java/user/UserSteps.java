package user;

import base.HttpClient;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static base.Constants.*;
import static io.restassured.RestAssured.given;

public class UserSteps extends HttpClient {

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(USER_REGISTER)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(User user) {

        UserCredentials credentials = UserCredentials.credentialsFromUser(user);

        return given()
                .spec(requestSpecification())
                .and()
                .body(credentials)
                .when()
                .post(USER_LOGIN)
                .then();
    }

    @Step("Обновление данных пользователя без авторизации")
    public ValidatableResponse updateUserWithoutAuthorization(User user) {

        UserCredentials credentials = UserCredentials.credentialsFromUser(user);

        return given()
                .spec(requestSpecification())
                .and()
                .body(credentials)
                .when()
                .patch(USER)
                .then();
    }

    @Step("Обновление данных пользователя после авторизации")
    public ValidatableResponse updateUserAfterAuthorization(User user, String accessToken) {

        UserCredentials credentials = UserCredentials.credentialsFromUser(user);

        return given()
                .spec(requestSpecification())
                .and()
                .header("authorization", accessToken)
                .body(credentials)
                .when()
                .patch(USER)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse delete(String accessToken) {

        return given()
                .spec(requestSpecification())
                .and()
                .header("authorization", accessToken)
                .when()
                .delete(USER)
                .then();
    }
}