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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.eclipse.edc.api.management.schema.ManagementApiJsonSchema;
import org.eclipse.edc.catalog.api.query.CatalogResponseSchema;

@OpenAPIDefinition(info = @Info(version = "v4beta"))
@Tag(name = "Federated Catalog Query v4beta")
public interface FederatedCatalogApiV4 {

    @Operation(description = "Queries cached catalogs from the local federated catalog cache",
            requestBody = @RequestBody(content = @Content(schema = @Schema(ref = ManagementApiJsonSchema.V4.QUERY_SPEC))),
            parameters = @Parameter(name = "flatten", description = "Whether the resulting root catalog should be flattened"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "The cached catalogs matching the query",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogResponseSchema.class)))),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(ref = ManagementApiJsonSchema.V4.API_ERROR))))
            })
    JsonArray queryCachedCatalogV4(JsonObject querySpecJson, boolean flatten);
}
