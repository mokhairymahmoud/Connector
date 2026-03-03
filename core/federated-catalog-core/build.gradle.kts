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
    `java-test-fixtures`
}

dependencies {
    api(project(":core:crawler-core"))
    api(project(":spi:common:core-spi"))
    api(project(":spi:common:json-ld-spi"))
    api(project(":spi:common:participant-context-single-spi"))
    api(project(":spi:common:transform-spi"))
    api(project(":spi:control-plane:catalog-spi"))
    api(project(":spi:federated-catalog-spi"))
    api(project(":core:common:lib:catalog-util-lib"))
    api(libs.jackson.databind)

    implementation(project(":core:common:lib:query-lib"))
    implementation(project(":core:common:lib:store-lib"))
    implementation(project(":core:common:lib:util-lib"))

    testImplementation(project(":core:common:junit"))
    testImplementation(libs.awaitility)

    testImplementation(testFixtures(project(":spi:federated-catalog-spi")))
    testImplementation(testFixtures(project(":spi:crawler-spi")))

    testFixturesImplementation(project(":core:common:lib:json-ld-lib"))
    testFixturesImplementation(project(":spi:common:participant-spi"))
    testFixturesImplementation(project(":spi:common:policy-model"))
}
