package org.rosuda.ui.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class MockedViewMethodFactory {

    private static final Log LOG = LogFactory.getLog(MockedViewMethodFactory.class);

    static Object createDelegate(final Class<?> methodReturnClass) {
	final StringBuilder className = new StringBuilder("org.rosuda.ui.test.mock.").append(methodReturnClass.getSimpleName()).append("Mock");
	try {
	    final Class<?> mockDelegateClass = Class.forName(className.toString());
	    return mockDelegateClass.newInstance();
	} catch (Exception e) {
	    LOG.error("could not create mock for " + methodReturnClass, e);
	}
	return null;
    }
}
