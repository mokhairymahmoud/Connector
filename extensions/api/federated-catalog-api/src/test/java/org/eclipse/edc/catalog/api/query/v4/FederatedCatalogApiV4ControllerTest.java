/*
 *  Copyright (c) 2026 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - management API integration
 *
 */

package org.eclipse.edc.catalog.api.query.v4;

import io.restassured.specification.RequestSpecification;
import org.eclipse.edc.catalog.api.query.BaseFederatedCatalogApiControllerTest;

import static io.restassured.RestAssured.given;

public class FederatedCatalogApiV4ControllerTest extends BaseFederatedCatalogApiControllerTest {

    @Override
    protected Object controller() {
        return new FederatedCatalogApiV4Controller(queryService, transformerRegistry, validatorRegistry);
    }

    @Override
    protected RequestSpecification baseRequest() {
        return given()
                .baseUri("http://localhost:" + port + "/v4beta")
                .when();
    }
}
