package com.xxx.javatest_demo1.api;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Test(groups = {"api", "regression"})
public class AnonymousBetApiTest extends BetApiTest {

    /* 故意不写 @BeforeClass，不login，不碰token */
    @Test(description = "无token创建投注，断言网关401或403")
    public void testCreateBetNoToken() {
        given(authSpec(null)) // 真·不传Authorization header
                .body("{\"race_id\":\"R001\",\"stake\":10.0}")
                .when().post("/api/v1/bets")
                .then().statusCode(anyOf(is(401), is(403)));
    }
}
