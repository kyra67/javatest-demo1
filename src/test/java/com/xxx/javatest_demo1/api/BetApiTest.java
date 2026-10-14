package com.xxx.javatest_demo1.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BetApiTest {
	
	@BeforeAll
	static void setup() {
		// 启动 mock server
        MockServer.start();
        
		RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = 5000;
	}
	
	@Test
	@Order(1)
	@Tag("smoke")
	@DisplayName("API-下注成功")
	void testCreateBetSuccess() {
		given()
			.header("Authorization", "Bearer token_abc123")
			.contentType(ContentType.JSON)
			.body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":100}")
		.when()
			.post("/api/v1/bets")
		.then()
			.statusCode(201)
			.body("bet_id", notNullValue())
			.body("status", equalTo("ACCEPTED"));
	}
	
	@Test
    @Order(2)
    @Tag("regression")
    @DisplayName("API-无token返回401")
	void testCreateBetNoToken() {
		given()
        	.contentType(ContentType.JSON)
        	.body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":100}")
        .when()
        	.post("/api/v1/bets")
        .then()
        	.statusCode(401);
	}
	
	@Test
    @Order(3)
    @Tag("regression")
    @DisplayName("API-金额过小返回400")
    void testCreateBetInvalidAmount() {
        given()
            .header("Authorization", "Bearer token_abc123")
            .contentType(ContentType.JSON)
            .body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":9}")
        .when()
            .post("/api/v1/bets")
        .then()
            .statusCode(400);
    }
}
