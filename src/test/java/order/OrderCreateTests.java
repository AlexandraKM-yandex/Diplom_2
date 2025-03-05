package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserSteps;

import java.util.ArrayList;
import java.util.List;

import static base.Constants.LOGIN_URL;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static user.UserGenerator.randomUser;

public class OrderCreateTests {

    private User user;
    private UserSteps userSteps;
    private OrderSteps orderSteps;
    private List<String> ingredients;
    private String accessToken;

    String ingredientId1;
    String ingredientId2;
    String ingredientId3;
    String incorrectIngredient = randomAlphabetic(25);

    ValidatableResponse loginResponce;
    ValidatableResponse response;

    @Before
    public void setUp() {
        user = randomUser();
        userSteps = new UserSteps();
        userSteps.create(user);

        orderSteps = new OrderSteps();
        ingredients = new ArrayList<>();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с кодом 200 и успешным ответом")
    public void createOrderWithAuthorization() {
        loginResponce = userSteps.login(user);
        accessToken = loginResponce.extract().path("accessToken");

        ingredientId1 = orderSteps.getIngredientsIds().get(1);
        ingredientId2 = orderSteps.getIngredientsIds().get(2);
        ingredientId3 = orderSteps.getIngredientsIds().get(3);

        ingredients.add(ingredientId1);
        ingredients.add(ingredientId2);
        ingredients.add(ingredientId3);

        response = orderSteps.orderCreateAfterAuthorization(ingredients, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.number", isA(Integer.class));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка статуса 401 и поля 'message': You should be authorised")
    public void createOrderWithoutAuthorization() {
        ingredientId1 = orderSteps.getIngredientsIds().get(1);
        ingredientId2 = orderSteps.getIngredientsIds().get(2);
        ingredientId3 = orderSteps.getIngredientsIds().get(3);

        ingredients.add(ingredientId1);
        ingredients.add(ingredientId2);
        ingredients.add(ingredientId3);

        response = orderSteps.orderCreateWithoutAuthorization(ingredients);
        response.log().all()
                .assertThat()
                .statusCode(oneOf(SC_MOVED_PERMANENTLY, SC_MOVED_PERMANENTLY,
                        SC_SEE_OTHER, SC_TEMPORARY_REDIRECT))
                .header("Location", equalTo(LOGIN_URL));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией")
    @Description("Проверка статуса 400 и поля 'message': Ingredient ids must be provided")
    public void createOrderWithoutIngredients() {
        loginResponce = userSteps.login(user);
        accessToken = loginResponce.extract().path("accessToken");

        ingredientId1 = orderSteps.getIngredientsIds().get(1);
        ingredients.add(ingredientId1);
        ingredients.clear();

        response = orderSteps.orderCreateAfterAuthorization(ingredients, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с недоступными ингредиентами при авторизации")
    @Description("Проверка статуса 500 и поля 'success': false")
    public void createOrderWithInvalidIngredients() {
        loginResponce = userSteps.login(user);
        accessToken = loginResponce.extract().path("accessToken");

        ingredients.add(incorrectIngredient);

        response = orderSteps.orderCreateAfterAuthorization(ingredients, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    @Description("Удаление созданного пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userSteps.delete(accessToken);
        }
    }
}