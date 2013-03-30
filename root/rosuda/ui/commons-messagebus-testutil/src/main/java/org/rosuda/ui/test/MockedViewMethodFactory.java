package org.rosuda.ui.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MockedViewMethodFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockedViewMethodFactory.class);

    static Object createDelegate(final Class<?> methodReturnClass) {
        if (void.class.isAssignableFrom(methodReturnClass)) {
            return null;
        }
        final StringBuilder className = new StringBuilder("org.rosuda.ui.test.mock.").append(methodReturnClass.getSimpleName()).append(
                "Mock");
        try {
            final Class<?> mockDelegateClass = Class.forName(className.toString());
            return mockDelegateClass.newInstance();
        } catch (Exception e) {
            LOGGER.error("could not create mock for " + methodReturnClass, e);
        }
        return null;
    }
}
