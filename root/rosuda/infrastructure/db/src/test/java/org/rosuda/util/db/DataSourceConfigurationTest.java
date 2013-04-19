package org.rosuda.util.db;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.util.process.ShellContext;

public class DataSourceConfigurationTest {

    private DataSourceConfiguration dataSourceConfig;
    private ShellContext shellContext;
    static final String A_DERBY_URL = "jdbc:derby://localhost:3529//derby;create=true";
    
    @Before
    public void setUp() {
	this.dataSourceConfig = new DataSourceConfiguration();
	dataSourceConfig.setUrl(A_DERBY_URL);
	shellContext = mock(ShellContext.class);
    }
    
    @Test
    public void theConfigPropertiesAreSet() {
	assertThat(dataSourceConfig.getUrl(), equalTo(A_DERBY_URL));
    }
    
    @Test
    public void theDatabaseCanBeAdjustedByTheShellEnvironment() {
	when(shellContext.getEnvironmentVariable(eq(DerbyContext.DERBY_HOST))).thenReturn("mockedhost");
	when(shellContext.getEnvironmentVariable(eq(DerbyContext.DERBY_PORT))).thenReturn("mockedport");
	dataSourceConfig.processEnvironmentConfiguration(shellContext);
	assertThat(dataSourceConfig.getUrl(), equalTo("jdbc:derby://mockedhost:mockedport//derby;create=true"));
    }
    
    @Test
    public void theDatabaseCanOverridePortOnly() {
	when(shellContext.getEnvironmentVariable(eq(DerbyContext.DERBY_HOST))).thenReturn(null);
	when(shellContext.getEnvironmentVariable(eq(DerbyContext.DERBY_PORT))).thenReturn("mockedport");
	dataSourceConfig.processEnvironmentConfiguration(shellContext);
	assertThat(dataSourceConfig.getUrl(), equalTo("jdbc:derby://localhost:mockedport//derby;create=true"));
    }
    
    @Test
    public void theDatabaseCanOverrideHostOnly() {
	when(shellContext.getEnvironmentVariable(eq(DerbyContext.DERBY_HOST))).thenReturn("mockedhost");
	when(shellContext.getEnvironmentVariable(eq(DerbyContext.DERBY_PORT))).thenReturn(null);
	dataSourceConfig.processEnvironmentConfiguration(shellContext);
	assertThat(dataSourceConfig.getUrl(), equalTo("jdbc:derby://mockedhost:3529//derby;create=true"));
    }
}
