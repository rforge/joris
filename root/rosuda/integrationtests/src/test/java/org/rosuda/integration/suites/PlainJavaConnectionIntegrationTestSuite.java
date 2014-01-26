package org.rosuda.integration.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.rosuda.integration.connection.jrengine.CreateConnectionIntegrationTest;
import org.rosuda.integration.connection.jrengine.REngineAssignIntegrationTest;
import org.rosuda.integration.connection.jrengine.WrappedEngineFormatIntegrationTest;
import org.rosuda.integration.connection.jrengine.WrappedEngineIntegrationTest;
import org.rosuda.integration.suites.util.AfterPlainJavaConnectionIntegrationTestSuite;
import org.rosuda.integration.suites.util.BeforePlainJavaConnectionIntegrationTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
    BeforePlainJavaConnectionIntegrationTestSuite.class,
    CreateConnectionIntegrationTest.class,
    REngineAssignIntegrationTest.class,
    WrappedEngineFormatIntegrationTest.class,
    WrappedEngineIntegrationTest.class,
    AfterPlainJavaConnectionIntegrationTestSuite.class
})
public class PlainJavaConnectionIntegrationTestSuite {

}
