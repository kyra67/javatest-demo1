package com.xxx.javatest_demo1.api;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class AuthenticateBetApiTest extends BetApiTest {
	
	private String classToken;
	
	// 1. 登录只跑一次，alwaysRun = true保命（smoke分组不干掉它）
	@BeforeClass(alwaysRun = true)
	public void loginOnce() {
		this.classToken = given()
                .contentType("application/json")
                .body("{\"username\":\"qa_user\",\"password\":\"qa_pass\"}")
                .when().post("/api/v1/login")
                .then().statusCode(200)
                .extract().path("data.token");
	}
	
        // 2. DataProvider：依赖 classToken 已就位，TestNG保证顺序：BeforeClass -> DP -> @Test
        @DataProvider(name = "betScenarios")
         public Object[][] betScenarios() {
                return new Object[][] {
                // raceId,    stake,  expectedHttpStatus,  desc
                {"R001",      10.0,   201,                 "正常投注"},
                {"R002",      0.5,    400,                 "低于最小限额"},
                {"INVALID99", 50.0,   404,                 "不存在的赛事ID"},
                {"R003",      99999,  400,                 "超单笔上限"}
        };
    }
    
    // 3. 数据驱动主用例（regression跑全量，smoke不include这个DP用例）
    @Test(dataProvider = "betScenarios",
          groups = {"regression"},
          description = "带token创建投注-多场景数据驱动")
    public void testCreateBetWithToken(String raceId, double stake,
                                       int expectedStatus, String desc) {
        given(authSpec(this.classToken))
                .body(String.format(
                        "{\"race_id\":\"%s\",\"stake\":%.2f}", raceId, stake))
                .when().post("/api/v1/bets")
                .then()
                .statusCode(expectedStatus)
                // 201成功时顺带校验bet_id非空
                .body(expectedStatus == 201 ? "data.bet_id" : "error_code",
                        expectedStatus == 201 ? notNullValue() : anything());
    }
    
    /* 4. 非DP的单独用例：查投注列表 */
    @Test(groups = {"smoke", "regression"}, dependsOnMethods = "testCreateBetWithToken")
    public void testQueryBetList() {
        given(authSpec(this.classToken))
                .when().get("/api/v1/bets")
                .then()
                .statusCode(200)
                .body("data", notNullValue());
    }
}
