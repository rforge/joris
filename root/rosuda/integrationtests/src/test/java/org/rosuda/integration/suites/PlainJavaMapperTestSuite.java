package org.rosuda.integration.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.rosuda.integration.connection.mapper.RFilterIntegrationTest;
import org.rosuda.integration.connection.mapper.RModelSerializationIntegrationTest;
import org.rosuda.integration.connection.mapper.RRestructureIntegrationTest;
import org.rosuda.integration.connection.mapper.RTypeConversionIntegrationTest;
import org.rosuda.integration.suites.util.AfterPlainJavaConnectionTestSuite;
import org.rosuda.integration.suites.util.BeforePlainJavaConnectionTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
    BeforePlainJavaConnectionTestSuite.class,
    RFilterIntegrationTest.class,
    RModelSerializationIntegrationTest.class,
    RRestructureIntegrationTest.class,
    RTypeConversionIntegrationTest.class,
    AfterPlainJavaConnectionTestSuite.class
})
public class PlainJavaMapperTestSuite {

}
