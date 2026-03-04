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

package org.eclipse.edc.catalog.api.query.v3;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.eclipse.edc.catalog.api.query.BaseFederatedCatalogApiController;
import org.eclipse.edc.catalog.spi.QueryService;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/v3/catalog")
public class FederatedCatalogApiV3Controller extends BaseFederatedCatalogApiController implements FederatedCatalogApiV3 {

    public FederatedCatalogApiV3Controller(QueryService queryService, TypeTransformerRegistry transformerRegistry,
                                           JsonObjectValidatorRegistry validatorRegistry) {
        super(queryService, transformerRegistry, validatorRegistry);
    }

    @POST
    @Path("/query")
    @Override
    public JsonArray queryCachedCatalogV3(JsonObject querySpecJson, @DefaultValue("false") @QueryParam("flatten") boolean flatten) {
        return queryCachedCatalog(querySpecJson, flatten);
    }
}
