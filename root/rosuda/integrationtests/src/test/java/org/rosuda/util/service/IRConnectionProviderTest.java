package org.rosuda.util.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rosuda.REngine.Rserve.RConnection;

public class IRConnectionProviderTest {

    private ServiceManager serviceManager;

    @Before
    public void setUp() {
        serviceManager = new ServiceManager();
    }

    @Ignore("wrong package -> not integrationTest")
    @Test
    public void iCanCreateMyConnection() {
        serviceManager.provide(RConnection.class);
        // ITwoWayConnection provided =
        // serviceManager.provide(ITwoWayConnection.class);
        // assertThat(provided, notNullValue());
    }
}
