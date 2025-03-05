package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserSteps;

import static java.util.Optional.empty;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.is;
import static user.UserGenerator.randomUser;


public class GetOrderTest {

    private User user;
    private UserSteps userSteps;
    private OrderSteps orderSteps;
    private String accessToken;

    ValidatableResponse response;
    ValidatableResponse loginResponse;

    @Before
    @Description("Регистрация нового пользователя и получение токена")
    public void setUp() {
        user = new User();
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
    }

    @Test
    @DisplayName("Получение заказа для авторизированного пользователя")
    @Description("Проверка успешного получения заказа с кодом 200 и полем 'success': true")
    public void getOrdersAfterAuthorizationTest() {
        user = randomUser();
        userSteps.create(user);
        loginResponse = userSteps.login(user);
        accessToken = loginResponse.extract().path("accessToken");
        response = orderSteps.getOrdersAfterAuthorization(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("orders", Matchers.not(empty()));
    }

    @Test
    @DisplayName("Получение заказа для неавторизированного пользователя")
    @Description("Проверка статуса 401 и поля 'message': You should be authorised")
    public void getOrdersWithoutAuthorizationErrorTest() {
        user = randomUser();
        userSteps.create(user);
        response = orderSteps.getOrdersWithoutAuthorization();
        response.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @After
    @Description("Удаление созданного пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userSteps.delete(accessToken);
        }
    }
}