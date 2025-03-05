package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static user.UserGenerator.randomUser;

public class UserLoginTests {

    private User user;
    private User newUser;
    private UserSteps userSteps;
    private String accessToken;

    ValidatableResponse response;

    @Before
    public void setUp() {
        user = randomUser();
        userSteps = new UserSteps();
        userSteps.create(user);
        response = userSteps.create(user);
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Авторизация с корректными данными")
    @Description("Проверка статуса 200 и поля 'success': true")
    public void shouldLoginUserWithValidCredentials() {
        response = userSteps.login(user);
        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", isA(String.class));
    }

    @Test
    @DisplayName("Авторизация с неверным email")
    @Description("Проверка статуса 401 и поля 'message': email or password are incorrect")
    public void shouldFailLoginWithInvalidEmail() {
        newUser = new User(randomUser().getEmail(), user.getPassword(), user.getName());

        response = userSteps.login(newUser);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация без ввода почты")
    @Description("Проверка статуса 401 и поля 'message': email or password are incorrect")
    public void shouldFailLoginWithoutEmail() {
        user = new User(null, randomUser().getPassword(), randomUser().getName());

        response = userSteps.login(user);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    @Description("Проверка статуса 401 и поля 'message': email or password are incorrect")
    public void shouldFailLoginWithInvalidPassword() {
        newUser = new User(user.getEmail(), randomUser().getPassword(), user.getName());

        response = userSteps.login(newUser);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация без ввода пароля")
    @Description("Проверка статуса 401 и поля 'message': email or password are incorrect")
    public void shouldFailLoginWithoutPassword() {
        user = new User(randomUser().getEmail(), null, randomUser().getName());
        response = userSteps.login(user);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));

    }

    @After
    @Description("Удаление созданного пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userSteps.delete(accessToken);
        }
    }
}