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

package org.eclipse.edc.catalog.api.query;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.eclipse.edc.catalog.spi.QueryService;
import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.federatedcatalog.util.FederatedCatalogUtil;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.AbstractResult;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;

import static jakarta.json.stream.JsonCollectors.toJsonArray;
import static org.eclipse.edc.spi.query.QuerySpec.EDC_QUERY_SPEC_TYPE;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

public abstract class BaseFederatedCatalogApiController {

    private final QueryService queryService;
    private final TypeTransformerRegistry transformerRegistry;
    private final JsonObjectValidatorRegistry validatorRegistry;

    protected BaseFederatedCatalogApiController(QueryService queryService, TypeTransformerRegistry transformerRegistry,
                                                JsonObjectValidatorRegistry validatorRegistry) {
        this.queryService = queryService;
        this.transformerRegistry = transformerRegistry;
        this.validatorRegistry = validatorRegistry;
    }

    protected JsonArray queryCachedCatalog(JsonObject querySpecJson, boolean flatten) {
        QuerySpec querySpec;
        if (querySpecJson == null) {
            querySpec = QuerySpec.none();
        } else {
            validatorRegistry.validate(EDC_QUERY_SPEC_TYPE, querySpecJson).orElseThrow(ValidationFailureException::new);
            querySpec = transformerRegistry.transform(querySpecJson, QuerySpec.class)
                    .orElseThrow(InvalidRequestException::new);
        }

        return queryService.getCatalog(querySpec)
                .orElseThrow(exceptionMapper(Catalog.class))
                .stream()
                .map(catalog -> flatten ? FederatedCatalogUtil.flatten(catalog) : catalog)
                .map(catalog -> transformerRegistry.transform(catalog, JsonObject.class))
                .filter(Result::succeeded)
                .map(AbstractResult::getContent)
                .collect(toJsonArray());
    }
}
