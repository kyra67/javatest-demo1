package com.xxx.javatest_demo1.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.testng.annotations.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class BetApiTest {
	
	@BeforeClass
	static void setup() {
		// 启动 mock server
        MockServer.start();
        
		RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = 5000;
	}
	
	@Test(priority = 1, description = "下注成功")
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
	
	@Test(priority = 2, description = "API-无token返回401")
	void testCreateBetNoToken() {
		given()
        	.contentType(ContentType.JSON)
        	.body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":100}")
        .when()
        	.post("/api/v1/bets")
        .then()
        	.statusCode(401);
	}
	
	@Test(priority = 2, description = "API-金额过小返回400")
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
