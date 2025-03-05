package base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static base.Constants.BASE_URL;

public class HttpClient {

    protected RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build()
                .filter(new AllureRestAssured())
                .log().all();
    }
}