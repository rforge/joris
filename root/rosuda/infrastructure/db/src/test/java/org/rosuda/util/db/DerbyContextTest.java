package org.rosuda.util.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.*;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.util.process.ShellContext;

public class DerbyContextTest {

    public class TestDataSourceConfiguration extends DataSourceConfiguration {
	private String port;
	
	@Override
	public String getPort() {
	    return port;
	}
    }

    private class TestShellContext extends ShellContext {
	
	private String property = null;
	private String classpath = "classpath";
	
	@Override
	public String getClasspath() {
	    return classpath;
	}
	
	@Override
	public String getProperty(String propertyName) {
	    return property;
	}
    }

    private DerbyContext derbyContext;
    private TestShellContext environmentStub;
    private TestDataSourceConfiguration datasourceConfigurationStub;
    
    @Before
    public void setUp() {
	derbyContext = new DerbyContext();
	environmentStub = new TestShellContext();
	derbyContext.setShellContext(environmentStub);
	datasourceConfigurationStub = new TestDataSourceConfiguration();
	derbyContext.setDataSourceConfiguration(datasourceConfigurationStub );
    }
    
    @Test
    public void theTemplateStringCreatesAnEnvironmentSpecificShellCall() {
	datasourceConfigurationStub.port = "1600";
	environmentStub.property = "DERBY_PROPERTY";

	assertEquals("arg1 classpath arg2 1600 arg3 DERBY_PROPERTY",derbyContext.createShellCall("arg1 {0} arg2 {1} arg3 {2}"));
    }
    
    @Test
    public void whenAShellPropertyIsNullTheArgumentIsReplacedByEmptyString() {
	datasourceConfigurationStub.port = "1600";
	environmentStub.property = null;
	
	assertEquals("arg1 classpath arg2 1600 arg3 ",derbyContext.createShellCall("arg1 {0} arg2 {1} arg3 {2}"));	
    }
    
    @Test
    public void theReferenceCommandFormatWorksWithoutEnvironmentVariable() {
	datasourceConfigurationStub.port = "3529";
	environmentStub.classpath = System.getProperty("java.class.path");
	environmentStub.property = null;
	
	assertThat(derbyContext.createShellCall("java -cp {0} org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager -p {1} {2}"),
		CombinableMatcher.<String>both(startsWith("java -cp ")).and(endsWith(" org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager -p 3529 ")));
		
    }
    
    @Test
    public void theReferenceCommandFormatWorksWithEnvironmentVariable() {
	
	datasourceConfigurationStub.port = "3529";
	environmentStub.classpath = System.getProperty("java.class.path");
	environmentStub.property = "-h $OPENSHIFT_INTERNAL_IP";
	
	assertThat(derbyContext.createShellCall("java -cp {0} org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager -p {1} {2}"),
		CombinableMatcher.<String>both(startsWith("java -cp ")).and(
			endsWith(" org.apache.derby.drda.NetworkServerControl shutdown -noSecurityManager -p 3529 -h $OPENSHIFT_INTERNAL_IP")));
	
    }
}
