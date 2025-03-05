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

public class UserCreateTests {

    private User user;
    private User userCopy;
    private UserSteps userSteps;
    String accessToken;

    ValidatableResponse response;
    ValidatableResponse response2;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    @Description("Проверка статуса 200 и поля 'success': true")
    public void shouldRegisterNewUserSuccessfully() {
        user = randomUser();
        response = userSteps.create(user);
        accessToken = response.extract().path("accessToken");
        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", isA(String.class));

    }

    @Test
    @DisplayName("Попытка регистрации уже существующего пользователя")
    @Description("Проверка статуса 403 и поля 'message': User already exists")
    public void shouldFailToRegisterExistingUser() {

        user = randomUser();
        response = userSteps.create(user);
        accessToken = response.extract().path("accessToken");
        userCopy = new User(user.getEmail(), user.getPassword(), user.getName());
        response2 = userSteps.create(userCopy);
        response2.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));

    }

    @Test
    @DisplayName("Регистрация пользователя без email")
    @Description("Проверка статуса 403 и поля 'message': Email, password and name are required fields")
    public void shouldFailToRegisterUserWithoutEmail() {
        user = new User(null, randomUser().getPassword(), randomUser().getName());
        userSteps = new UserSteps();
        response = userSteps.create(user);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    @Description("Проверка статуса 403 и поля 'message': Email, password and name are required fields")
    public void shouldFailToRegisterUserWithoutPassword() {
        user = new User(randomUser().getEmail(), null, randomUser().getName());
        userSteps = new UserSteps();
        response = userSteps.create(user);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));

    }

    @After
    @Description("Удаление созданного пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userSteps.delete(accessToken);
        }
    }
}