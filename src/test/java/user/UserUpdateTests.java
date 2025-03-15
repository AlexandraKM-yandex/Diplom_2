package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static user.UserGenerator.randomUser;

public class UserUpdateTests {

    private User user;
    private User newUser;
    private UserSteps userSteps;
    private String accessToken;

    ValidatableResponse loginResponse;
    ValidatableResponse response;

    @Before
    public void setUp() {
        user = randomUser();
        userSteps = new UserSteps();
        userSteps.create(user);
    }

    @Test
    @DisplayName("Изменение email без авторизации")
    @Description("Проверка изменения email без авторизации, ожидается ошибка 401")
    public void shouldReturnUnauthorizedWhenUpdatingEmailWithoutAuth() {
        user.setEmail(randomUser().getEmail());
        response = userSteps.updateUserWithoutAuthorization(user);
        response.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение имени без авторизации")
    @Description("Проверка изменения имени без авторизации, ожидается ошибка 401")
    public void shouldReturnUnauthorizedWhenUpdatingNameWithoutAuth() {
        user.setName(randomUser().getName());
        response = userSteps.updateUserWithoutAuthorization(user);
        response.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }


    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    @Description("Проверка статуса 200 и поля 'success': true")
    public void shouldUpdateEmailWhenUserIsAuthorized() {
        loginResponse = userSteps.login(user);
        accessToken = loginResponse.extract().path("accessToken");
        user.setEmail(randomUser().getEmail());
        response = userSteps.updateUserAfterAuthorization(user, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(user.getEmail().toLowerCase().toString()));
    }

    @Test
    @DisplayName("Попытка изменить email на уже использованный после авторизации")
    @Description("Проверка изменения email на уже использованный, ожидается ошибка 403 с сообщением о конфликте email")
    public void shouldReturnForbiddenWhenEmailIsUsedWithAuth() {
        loginResponse = userSteps.login(user);
        accessToken = loginResponse.extract().path("accessToken");
        newUser = randomUser();
        userSteps.create(newUser);
        user.setEmail(newUser.getEmail());
        response = userSteps.updateUserAfterAuthorization(user, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User with such email already exists"));
    }

    @Test
    @DisplayName("Изменение имени после авторизации")
    @Description("Проверка успешного изменения имени после авторизации, ожидается статус 200 и успешное изменение")
    public void shouldUpdateNameWithAuth() {
        loginResponse = userSteps.login(user);
        accessToken = loginResponse.extract().path("accessToken");
        user.setName(randomUser().getName());
        response = userSteps.updateUserAfterAuthorization(user, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.name", is(user.getName().toString()));
    }

    @After
    @Description("Удаление созданного пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userSteps.delete(accessToken);
        }
    }
}