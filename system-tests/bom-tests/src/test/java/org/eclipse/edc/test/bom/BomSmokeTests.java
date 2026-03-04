/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.edc.test.bom;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.eclipse.edc.junit.annotations.EndToEndTest;
import org.eclipse.edc.junit.extensions.EmbeddedRuntime;
import org.eclipse.edc.junit.extensions.RuntimeExtension;
import org.eclipse.edc.junit.extensions.RuntimePerMethodExtension;
import org.eclipse.edc.protocol.spi.DefaultParticipantIdExtractionFunction;
import org.eclipse.edc.spi.iam.AudienceResolver;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.iam.VerificationContext;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.util.io.Ports.getFreePort;
import static org.hamcrest.Matchers.equalTo;

public class BomSmokeTests {
    abstract static class SmokeTest {
        public static final String DEFAULT_PORT = "8080";
        public static final String DEFAULT_PATH = "/api";

        @Test
        void assertRuntimeReady() {
            await().untilAsserted(() -> given()
                    .baseUri("http://localhost:" + DEFAULT_PORT + DEFAULT_PATH + "/check/startup")
                    .get()
                    .then()
                    .statusCode(200)
                    .log().ifValidationFails()
                    .body("isSystemHealthy", equalTo(true)));

        }
    }

    private static Map<String, String> controlPlaneBaseSettings() {
        var settings = new HashMap<String, String>();
        settings.put("web.http.port", SmokeTest.DEFAULT_PORT);
        settings.put("web.http.path", SmokeTest.DEFAULT_PATH);
        settings.put("web.http.control.port", String.valueOf(getFreePort()));
        settings.put("web.http.control.path", "/api/control");
        settings.put("web.http.management.port", String.valueOf(getFreePort()));
        settings.put("web.http.management.path", "/api/management");
        return settings;
    }

    private static Map<String, String> controlPlaneDcpSettings() {
        var settings = controlPlaneBaseSettings();
        settings.put("edc.iam.sts.oauth.token.url", "https://sts.com/token");
        settings.put("edc.iam.sts.oauth.client.id", "test-client");
        settings.put("edc.iam.sts.oauth.client.secret.alias", "test-alias");
        settings.put("edc.iam.sts.privatekey.alias", "privatekey");
        settings.put("edc.iam.sts.publickey.id", "publickey");
        settings.put("edc.iam.issuer.id", "did:web:someone");
        return settings;
    }

    private static EmbeddedRuntime controlPlaneBaseRuntime(String name, String module) {
        return new EmbeddedRuntime(name, module)
                .registerServiceMock(IdentityService.class, mockIdentityService())
                .registerServiceMock(AudienceResolver.class, mockAudienceResolver())
                .registerServiceMock(DefaultParticipantIdExtractionFunction.class, mockParticipantIdExtractionFunction())
                .configurationProvider(() -> ConfigFactory.fromMap(controlPlaneBaseSettings()));
    }

    private static IdentityService mockIdentityService() {
        return new IdentityService() {
            @Override
            public Result<TokenRepresentation> obtainClientCredentials(String participantContextId, TokenParameters parameters) {
                return Result.success(TokenRepresentation.Builder.newInstance()
                        .token("test-token")
                        .expiresIn(300L)
                        .build());
            }

            @Override
            public Result<ClaimToken> verifyJwtToken(String participantContextId, TokenRepresentation tokenRepresentation, VerificationContext context) {
                return Result.success(ClaimToken.Builder.newInstance().build());
            }
        };
    }

    private static AudienceResolver mockAudienceResolver() {
        return message -> Result.success("test-audience");
    }

    private static DefaultParticipantIdExtractionFunction mockParticipantIdExtractionFunction() {
        return claimToken -> "test-participant";
    }

    @Nested
    class ControlPlaneBase extends SmokeTest {

        @RegisterExtension
        protected RuntimeExtension runtime = new RuntimePerMethodExtension(
                controlPlaneBaseRuntime("control-plane-base-bom", ":dist:bom:controlplane-base-bom")
        );
    }

    @Nested
    @EndToEndTest
    class ControlPlaneDcp extends SmokeTest {

        @RegisterExtension
        protected RuntimeExtension runtime = new RuntimePerMethodExtension(
                new EmbeddedRuntime("control-plane-dcp-bom", ":dist:bom:controlplane-dcp-bom")
                        .configurationProvider(() -> ConfigFactory.fromMap(controlPlaneDcpSettings()))
        );
    }

    @Nested
    class FederatedCatalogBase extends SmokeTest {

        @RegisterExtension
        protected RuntimeExtension runtime = new RuntimePerMethodExtension(
                controlPlaneBaseRuntime("federatedcatalog-base-bom", ":dist:bom:federatedcatalog-base-bom")
        );
    }

    @Nested
    class FederatedCatalogDcp extends SmokeTest {

        @RegisterExtension
        protected RuntimeExtension runtime = new RuntimePerMethodExtension(
                new EmbeddedRuntime("federatedcatalog-dcp-bom", ":dist:bom:federatedcatalog-dcp-bom")
                        .configurationProvider(() -> ConfigFactory.fromMap(controlPlaneDcpSettings()))
        );
    }

    @Nested
    @EndToEndTest
    public class DataPlaneBase extends SmokeTest {

        @RegisterExtension
        static WireMockExtension server = WireMockExtension.newInstance()
                .options(wireMockConfig().dynamicPort())
                .build();

        @RegisterExtension
        protected RuntimeExtension runtime =
                new RuntimePerMethodExtension(new EmbeddedRuntime("data-plane-base-bom", ":dist:bom:dataplane-base-bom")
                        .configurationProvider(() -> ConfigFactory.fromMap(Map.of(
                                "edc.transfer.proxy.token.verifier.publickey.alias", "test-alias",
                                "edc.transfer.proxy.token.signer.privatekey.alias", "private-alias",
                                "edc.dpf.selector.url", "http://localhost:%s/selector".formatted(server.getPort()),
                                "web.http.control.port", "8081",
                                "web.http.control.path", "/api/control",
                                "web.http.port", DEFAULT_PORT,
                                "web.http.path", DEFAULT_PATH)))
                );

        @BeforeEach
        void setup() {
            server.stubFor(post("/selector").willReturn(ok()));
        }
    }

}
