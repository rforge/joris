package org.rosuda.util.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.util.process.ShellContext;

public class DerbyContextTest {

    private DerbyContext derbyContext;
    private ShellContext environmentStub;
    private DataSourceConfiguration datasourceConfigurationStub;
    
    @Before
    public void setUp() {
	derbyContext = new DerbyContext();
	environmentStub = mock(ShellContext.class);
	derbyContext.setShellContext(environmentStub);
	datasourceConfigurationStub = new DataSourceConfiguration();
	datasourceConfigurationStub.setUrl(DataSourceConfigurationTest.A_DERBY_URL);
	derbyContext.setDataSourceConfiguration(datasourceConfigurationStub );
    }
    
    @Test
    public void theTemplateStringCreatesAnEnvironmentSpecificShellCall() {
	when(environmentStub.getClasspath()).thenReturn("classpath");
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_PORT))).thenReturn("1600");
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_HOST))).thenReturn("DERBY_PROPERTY");
	
	assertEquals("arg1 classpath arg2 -p 1600 -h DERBY_PROPERTY",derbyContext.createShellCall("arg1 {0} arg2{1}"));
    }
    
    @Test
    public void whenAShellPropertyIsNullTheArgumentIsReplacedByEmptyString() {
	when(environmentStub.getClasspath()).thenReturn("classpath");
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_PORT))).thenReturn("1600");
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_HOST))).thenReturn(null);
	
	assertEquals("arg1 classpath arg2 -p 1600",derbyContext.createShellCall("arg1 {0} arg2{1}"));
    }
    
    @Test
    public void whenNoPortIsSetFromEnvironmentThePortFromTheConfigIsUsed() {
	when(environmentStub.getClasspath()).thenReturn(System.getProperty("java.class.path"));
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_PORT))).thenReturn(null);
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_HOST))).thenReturn(null);
	
	assertThat(derbyContext.createShellCall("java -cp {0} org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager{1}"),
		CombinableMatcher.<String>both(startsWith("java -cp ")).and(endsWith(" org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager -p 3529")));
		
    }
    
    @Test
    public void theReferenceCommandFormatWorksWithoutEnvironmentVariable() {
	when(environmentStub.getClasspath()).thenReturn(System.getProperty("java.class.path"));
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_PORT))).thenReturn("3529");
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_HOST))).thenReturn(null);
	
	assertThat(derbyContext.createShellCall("java -cp {0} org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager{1}"),
		CombinableMatcher.<String>both(startsWith("java -cp ")).and(endsWith(" org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager -p 3529")));
		
    }
    
    @Test
    public void theReferenceCommandFormatWorksWithEnvironmentVariable() {
	
	when(environmentStub.getClasspath()).thenReturn(System.getProperty("java.class.path"));
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_PORT))).thenReturn("3529");
	when(environmentStub.getProperty(eq(DerbyContext.DERBY_HOST))).thenReturn("$OPENSHIFT_INTERNAL_IP");
	
	assertThat(derbyContext.createShellCall("java -cp {0} org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager{1}"),
		CombinableMatcher.<String>both(startsWith("java -cp ")).and(
			endsWith(" org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager -p 3529 -h $OPENSHIFT_INTERNAL_IP")));
	
    }
    
}
