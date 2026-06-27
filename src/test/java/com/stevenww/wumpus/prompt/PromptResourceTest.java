package com.stevenww.wumpus.prompt;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class PromptResourceTest {
    @Test
    void shouldEchoContextThroughApi() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"context\":\"hello\"}")
                .when()
                .post("/api/prompt")
                .then()
                .statusCode(200)
                .body("prompt", equalTo("hello"));
    }
}

