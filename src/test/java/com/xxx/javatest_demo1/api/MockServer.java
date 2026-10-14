package com.xxx.javatest_demo1.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;

public class MockServer {

    private static WireMockServer wireMockServer;

    public static void start() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            wireMockServer = new WireMockServer(5000); // 端口 5000，跟你测试里一致
            wireMockServer.start();

            configureFor("127.0.0.1", 5000);

            // ========== 路由 1：下注成功 ==========
            stubFor(post(urlEqualTo("/api/v1/bets"))
                .withHeader("Authorization", equalTo("Bearer token_abc123"))
                .withRequestBody(matchingJsonPath("$.amount", greaterThan("9")))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"bet_id\":\"BET_001\",\"status\":\"ACCEPTED\",\"race_id\":\"R001\",\"horse_id\":\"H01\"}")
                )
            );

            // ========== 路由 2：无 token → 401 ==========
            stubFor(post(urlEqualTo("/api/v1/bets"))
                .withHeader("Authorization", absent())
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"error\":\"Unauthorized\"}")
                )
            );

            // ========== 路由 3：金额过小 → 400 ==========
            stubFor(post(urlEqualTo("/api/v1/bets"))
                .withHeader("Authorization", equalTo("Bearer token_abc123"))
                .withRequestBody(matchingJsonPath("$.amount", equalTo("9")))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"error\":\"Amount must be >= 10\"}")
                )
            );

            System.out.println("✅ Mock Server started on http://127.0.0.1:5000");
        }
    }

    private static StringValuePattern greaterThan(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void stop() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            System.out.println("🛑 Mock Server stopped");
        }
    }
}
