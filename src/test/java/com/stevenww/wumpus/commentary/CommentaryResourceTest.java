package com.stevenww.wumpus.commentary;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
class CommentaryResourceTest {
    @Test
    void shouldReturnCommentaryThroughApi() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "action": "MOVE",
                          "targetRoom": 5,
                          "outcome": "SAFE",
                          "playerRoom": 5,
                          "adjacentRooms": [1, 2, 3],
                          "hazardWarnings": ["You smell a Wumpus."],
                          "arrowsRemaining": 5,
                          "movesTaken": 3,
                          "previousActionSummaries": ["Moved to room 4"]
                        }
                        """)
                .when()
                .post("/api/commentary")
                .then()
                .statusCode(200)
                .body("fallback", equalTo(true))
                .body("commentary", not(nullValue()));
    }

    @Test
    void shouldHandleEmptyPayload() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/commentary")
                .then()
                .statusCode(200)
                .body("fallback", equalTo(true))
                .body("commentary", not(nullValue()));
    }

    @Test
    void shouldApplyRateLimitWithDeterministicFallback() {
        Map<String, Object> requestBody = Map.of(
                "action", "MOVE",
                "targetRoom", 5,
                "outcome", "SAFE",
                "playerRoom", 5,
                "adjacentRooms", new int[]{1, 2, 3}
        );

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/commentary")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/commentary")
                .then()
                .statusCode(200)
                .body("fallback", equalTo(true))
                .body("commentary", equalTo("The narrator is catching their breath between disasters."));
    }
}
