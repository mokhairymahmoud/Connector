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
}

dependencies {
    api(project(":core:federated-catalog-core"))
    implementation(project(":core:common:lib:transform-lib"))
    implementation(project(":core:control-plane:control-plane-transform"))
    implementation(project(":data-protocols:dsp:dsp-lib:dsp-catalog-lib:dsp-catalog-transform-lib"))
    implementation(project(":data-protocols:dsp:dsp-2025:dsp-catalog-2025:dsp-catalog-transform-2025"))
    implementation(project(":spi:common:participant-spi"))

    testImplementation(project(":core:common:junit"))
    testImplementation(project(":core:common:lib:json-lib"))
    testImplementation(project(":core:common:lib:json-ld-lib"))
    testImplementation(project(":core:common:lib:transform-lib"))
    testImplementation(testFixtures(project(":spi:federated-catalog-spi")))
    testImplementation(testFixtures(project(":spi:crawler-spi")))
    testImplementation(testFixtures(project(":core:federated-catalog-core")))
}
