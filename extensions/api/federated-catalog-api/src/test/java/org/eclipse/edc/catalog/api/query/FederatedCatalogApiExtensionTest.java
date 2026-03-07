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

package org.eclipse.edc.catalog.api.query;

import org.eclipse.edc.catalog.api.query.v3.FederatedCatalogApiV3Controller;
import org.eclipse.edc.catalog.api.query.v4.FederatedCatalogApiV4Controller;
import org.eclipse.edc.catalog.spi.QueryService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DependencyInjectionExtension.class)
class FederatedCatalogApiExtensionTest {

    private final WebService webService = mock();
    private final TypeTransformerRegistry transformerRegistry = mock();
    private final JsonObjectValidatorRegistry validatorRegistry = mock();
    private final JsonLd jsonLd = mock();
    private final TypeManager typeManager = mock();
    private final QueryService queryService = mock();

    @BeforeEach
    void setUp(ServiceExtensionContext context) {
        context.registerService(WebService.class, webService);
        context.registerService(TypeTransformerRegistry.class, transformerRegistry);
        context.registerService(JsonObjectValidatorRegistry.class, validatorRegistry);
        context.registerService(JsonLd.class, jsonLd);
        context.registerService(TypeManager.class, typeManager);
        context.registerService(QueryService.class, queryService);
        when(transformerRegistry.forContext("management-api")).thenReturn(transformerRegistry);
    }

    @Test
    void initialize_shouldRegisterControllersOnManagementContext(FederatedCatalogApiExtension extension, ServiceExtensionContext context) {
        extension.initialize(context);

        verify(webService).registerResource(eq(ApiContext.MANAGEMENT), isA(FederatedCatalogApiV3Controller.class));
        verify(webService).registerResource(eq(ApiContext.MANAGEMENT), isA(FederatedCatalogApiV4Controller.class));
        verify(webService).registerDynamicResource(eq(ApiContext.MANAGEMENT), eq(FederatedCatalogApiV3Controller.class), any());
        verify(webService).registerDynamicResource(eq(ApiContext.MANAGEMENT), eq(FederatedCatalogApiV4Controller.class), any());
    }

    @Test
    void initialize_shouldUseManagementTransformerRegistry(FederatedCatalogApiExtension extension, ServiceExtensionContext context) {
        extension.initialize(context);

        verify(transformerRegistry).forContext("management-api");
    }
}
