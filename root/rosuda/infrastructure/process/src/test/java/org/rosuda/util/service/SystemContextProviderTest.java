package org.rosuda.util.service;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.util.system.SystemContext;

public class SystemContextProviderTest {

    private ServiceManager serviceManager;

    @Before
    public void setUp() {
        this.serviceManager = new ServiceManager();
    }

    @Test
    public void systemContextIsKnownForCurrentSystem() {
        assertThat(serviceManager.createProvider(SystemContext.class), notNullValue());
    }

    @Test
    public void atLeastOneProcessContainingEIsFound() {
        final Collection<String> processesContainingE = serviceManager.provide(SystemContext.class).runningProcesses("e");
        assertThat(processesContainingE, hasSize(greaterThanOrEqualTo(1)));
    }
}
