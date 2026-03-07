/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

plugins {
    `java-library`
    id(libs.plugins.swagger.get().pluginId)
}

dependencies {
    api(project(":spi:federated-catalog-spi"))
    api(project(":spi:common:transform-spi"))
    api(project(":spi:common:validator-spi"))
    api(project(":spi:common:web-spi"))

    implementation(project(":core:common:lib:api-lib"))
    implementation(project(":core:common:lib:catalog-util-lib"))
    implementation(project(":extensions:common:api:lib:management-api-lib"))
    implementation(project(":extensions:common:http:lib:jersey-providers-lib"))

    implementation(libs.jakarta.rsApi)

    testImplementation(project(":core:common:junit"))
    testImplementation(project(":core:common:lib:json-lib"))
    testImplementation(project(":core:common:lib:transform-lib"))
    testImplementation(project(":core:federated-catalog-core-2025"))
    testImplementation(project(":data-protocols:dsp:dsp-lib:dsp-catalog-lib:dsp-catalog-transform-lib"))
    testImplementation(project(":data-protocols:dsp:dsp-2025:dsp-catalog-2025:dsp-catalog-transform-2025"))
    testImplementation(project(":extensions:common:http"))
    testImplementation(testFixtures(project(":core:federated-catalog-core")))
    testImplementation(testFixtures(project(":extensions:common:http:jersey-core")))
    testImplementation(libs.restAssured)
}

edcBuild {
    swagger {
        apiGroup.set("management-api")
    }
}
