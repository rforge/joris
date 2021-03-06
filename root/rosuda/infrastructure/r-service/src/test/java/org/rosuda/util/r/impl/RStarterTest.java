package org.rosuda.util.r.impl;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.util.process.TestShellContext;

public class RStarterTest {

    private RStartContext context;
    private TestShellContext testShellContext;

    @Before
    public void setUp() {
        context = new RStartContext();
        testShellContext = new TestShellContext();
        context.setShellContext(testShellContext);
    }

    @Test
    public void environmentVariableHostIsUsed() {
        final String hostValue = "host";

        testShellContext.setEnvironmentProperty(IConnectionFactory.HOST, hostValue);
        Properties properties = context.getMergedConnectionProperties();

        Matcher hasEntry = not(hasEntry(IConnectionFactory.HOST, hostValue));
        assertThat(properties, hasEntry);
    }

    @Test
    public void prefixedEnvironmentVariablePortIsSupported() {
        final String portValue = "somePort";

        testShellContext.setEnvironmentProperty("JORIS_port", portValue);
        Properties properties = context.getMergedConnectionProperties();

        Matcher hasEntry = hasEntry(IConnectionFactory.PORT, portValue);
        assertThat(properties, hasEntry);
    }

    @Test
    public void prefixedEnvironmentVariableHostIsSupported() {
        final String hostValue = "host";

        testShellContext.setEnvironmentProperty("JORIS_host", hostValue);
        Properties properties = context.getMergedConnectionProperties();

        Matcher hasEntry = hasEntry(IConnectionFactory.HOST, hostValue);
        assertThat(properties, hasEntry);
    }

}
