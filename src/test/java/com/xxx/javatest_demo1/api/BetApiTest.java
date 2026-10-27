package com.xxx.javatest_demo1.api;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.testng.annotations.*;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.*;

import com.xxx.javatest_demo1.api.MockServer;


public abstract class BetApiTest {
	protected String classToken;
	
	@BeforeTest(alwaysRun = true)
	public void globalApiSetup() {
		// 启动 mock server
		// MockServer.start();
		
		// 本地mockserver
		RestAssured.baseURI = System.getProperty("api.base", "http://127.0.0.1:5000");
		
		// 端口单独管也行，但本地5000，CI里靠 -D 覆盖最干净
		if(System.getProperty("api.port") != null) {
			RestAssured.port = Integer.parseInt(System.getProperty("api.port"));
		}
		
		RestAssured.filters(
			new RequestLoggingFilter(),
			new ResponseLoggingFilter()
		);
	}
	
	@AfterTest(alwaysRun = true) 
	public void globalTeardown() {
		RestAssured.reset();
	}
	
	//* 条件化token：token==null 真·不传Authorization，根治 Bearer null 坑 */
	protected RequestSpecification authSpec(String token) {
		RequestSpecBuilder builder = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON);
		if (token != null && !token.isBlank()) {
			builder.addHeader("Authorization", "Bearer " + token);
		}
		
		return builder.build();
	}
	
//	@BeforeClass
//	static void setup() {
//		// 启动 mock server
//        MockServer.start();
//        
//		RestAssured.baseURI = "http://127.0.0.1";
//        RestAssured.port = 5000;
//	}
//	
//	@DataProvider(name = "betData")
//	public Object[][] privideBetData() {
//		return new Object[][] {
//			// { token, amount, expectedStatusCode, description }
//			{"Bearer token_abc123", 100, 201, "下注成功"},
//			{null,                  100, 401, "无token返回401"},
//			{"Bearer token_abc123",   9, 400, "金额过小返回400"}
//		};
//	}
//	
//	@Test(dataProvider = "betData", groups = {"api", "smoke"}, description = "下注成功主流程")
//	void testCreateBet(String token, int amount, int expectedStatus, String desc) {
//	    RequestSpecification req = given()
//	        .contentType(ContentType.JSON)
//	        .body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":" + amount + "}");
//	    if (token != null) {
//	    	req = req.header("Authorization", token);
//	    }
//	    req.when()
//	        .post("/api/v1/bets");
//	    req.then()
//	        .statusCode(expectedStatus)
//	        .body("status", (expectedStatus == 201) ? equalTo("ACCEPTED") : anything());
//	}
	
//	@Test(priority = 1, description = "下注成功")
//	void testCreateBetSuccess(String token, int amount, int expectedStatus, String desc) {
//		given()
//			.header("Authorization", Bearer token_abc123)
//			.contentType(ContentType.JSON)
//			.body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":100}")
//		.when()
//			.post("/api/v1/bets")
//		.then()
//			.statusCode(201)
//			.body("bet_id", notNullValue())
//			.body("status", equalTo("ACCEPTED"));
//	}
//	
//	@Test(groups = {"api", "regression-only"})
//	void testCreateBetNoToken() {
//		given()
//        	.contentType(ContentType.JSON)
//        	.body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":100}")
//        .when()
//        	.post("/api/v1/bets")
//        .then()
//        	.statusCode(org.hamcrest.Matchers.anyOf(
//                    org.hamcrest.Matchers.is(401),
//                    org.hamcrest.Matchers.is(403)
//                ));
//	}
//	
//	@Test(priority = 2, description = "API-金额过小返回400")
//    void testCreateBetInvalidAmount() {
//        given()
//            .header("Authorization", "Bearer token_abc123")
//            .contentType(ContentType.JSON)
//            .body("{\"race_id\":\"R001\",\"horse_id\":\"H01\",\"bet_type\":\"WIN\",\"amount\":9}")
//        .when()
//            .post("/api/v1/bets")
//        .then()
//            .statusCode(400);
//    }
}
