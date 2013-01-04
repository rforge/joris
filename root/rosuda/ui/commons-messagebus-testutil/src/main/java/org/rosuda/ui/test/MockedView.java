package org.rosuda.ui.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class MockedView<VIEW> implements InvocationHandler {

    final Map<Method, Object> methodDelegateMap = new HashMap<Method, Object>();
    
    MockedView(Class<? extends VIEW> viewClass) {
	for (final Method method: viewClass.getMethods()) {
	    methodDelegateMap.put(method, MockedViewMethodFactory.createDelegate(method.getReturnType()));
	}
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	final Object delegate = methodDelegateMap.get(method);
	if (delegate != null) {
	    return delegate;
	}
	return null;
    }

}
