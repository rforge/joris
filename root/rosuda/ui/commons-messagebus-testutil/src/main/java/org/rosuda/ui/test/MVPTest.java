package org.rosuda.ui.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MVP.Presenter;
import org.rosuda.ui.core.mvc.MessageBus;

public abstract class MVPTest<MODEL, VIEW, PRESENTER extends MVP.Presenter<MODEL, VIEW>, MODELINITIALIZER extends ModelInitializer<MODEL>> {

    protected final MODEL model;
    protected final VIEW view;
    protected final MODELINITIALIZER modelInitializer;

    @SuppressWarnings("unchecked")
    protected MVPTest() {
	final MessageBus mb = new MessageBus.Impl();
	final Type genericSuperclass = this.getClass().getGenericSuperclass();
	final Type[] classParameterTypes = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
	final PRESENTER presenter;
	try {
	    final Class<? extends MVP.Presenter<MODEL, VIEW>> presenterClass = (Class<? extends Presenter<MODEL, VIEW>>) getClassFromType(classParameterTypes[2]);
	    presenter = (PRESENTER) presenterClass.newInstance();
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
	MODELINITIALIZER initializer = null;
	try {
	    final Class<? extends ModelInitializer<MODEL>> modelInitializerClass = (Class<? extends ModelInitializer<MODEL>>) getClassFromType(classParameterTypes[3]);
	    initializer = (MODELINITIALIZER) modelInitializerClass.newInstance();
	} catch (final Exception e) {
	}
	modelInitializer = initializer;
	try {
	    final Class<? extends MODEL> modelClass = (Class<? extends MODEL>) getClassFromType(classParameterTypes[0]);
	    model = (MODEL) modelClass.newInstance();
	    if (initializer != null) {
		initializer.initModel(model);
	    }
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
	view = createMockTestView((Class<? extends VIEW>) getClassFromType(classParameterTypes[1]));
	presenter.bind(model, view, mb);
    }

    private VIEW createMockTestView(Class<? extends VIEW> viewClass) {
	return (VIEW) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{viewClass}, new MockedView<VIEW>(viewClass));
    }

    private Class<?> getClassFromType(final Type type) {
	if (type instanceof ParameterizedType) {
	    final ParameterizedType parameterizedType = (ParameterizedType) type;
	    return (Class<?>) parameterizedType.getRawType();
	} else if (type instanceof Class<?>) {
	    return (Class<?>) type;
	} else {
	    throw new RuntimeException("Parameter " + type + " is could not be bound to class or ParameterizedType");
	}
    }
}
