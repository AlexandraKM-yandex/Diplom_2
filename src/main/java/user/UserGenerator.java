package user;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;

public class UserGenerator {

    @Step
    public static User randomUser() {
        Faker faker = new Faker();
        faker.lordOfTheRings();
        String email = faker.internet().safeEmailAddress();
        String password = faker.internet().password(6, 10, true, true, true);
        String name = faker.lordOfTheRings().character();
        return new User(email, password, name);
    }
}