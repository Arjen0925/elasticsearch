/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.transport;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.shield.ShieldPlugin;
import org.elasticsearch.shield.transport.ShieldServerTransportService;
import org.elasticsearch.test.ShieldIntegTestCase;

import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;

// this class sits in org.elasticsearch.transport so that TransportService.requestHandlers is visible
public class ShieldServerTransportServiceTests extends ShieldIntegTestCase {
    @Override
    protected Settings transportClientSettings() {
        return Settings.settingsBuilder()
                .put(super.transportClientSettings())
                .put(ShieldPlugin.ENABLED_SETTING_NAME, true)
                .build();
    }

    public void testShieldServerTransportServiceWrapsAllHandlers() {
        for (TransportService transportService : internalTestCluster().getInstances(TransportService.class)) {
            assertThat(transportService, instanceOf(ShieldServerTransportService.class));
            for (Map.Entry<String, RequestHandlerRegistry> entry : transportService.requestHandlers.entrySet()) {
                assertThat(
                        "handler not wrapped by " + ShieldServerTransportService.ProfileSecuredRequestHandler.class + "; do all the handler registration methods have overrides?",
                        entry.getValue().getHandler(),
                        instanceOf(ShieldServerTransportService.ProfileSecuredRequestHandler.class)
                );
            }
        }
    }
}
