package org.rosuda.util.service;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ServiceManagerTest {

    private ServiceManager serviceManager;

    @Before
    public void setUp() {
        serviceManager = new ServiceManager();
    }

    @Test
    public void whenTheSystemServiceIsAvailableAServiceIsReturned() {
        ServiceProvider<Integer> someServiceProvider = registerServiceProvider();
        when(someServiceProvider.isReady(any(ServiceManager.class))).thenReturn(true);
        serviceManager.provide(Integer.class);
        Integer providedService = serviceManager.provide(Integer.class);
        assertThat(providedService, notNullValue());
    }

    @Test
    public void whenTheSystemServiceIsNotRunningTheStartProcessIsInvoked() {
        ServiceProvider<Integer> someServiceProvider = registerServiceProvider();
        when(someServiceProvider.isReady(any(ServiceManager.class))).thenReturn(false);
        serviceManager.provide(Integer.class);
        verify(someServiceProvider, times(1)).ready(any(ServiceManager.class));
    }

    @Test
    public void whenADependantServiceIsNotFoundThisServiceIsCreatedFirst() {
        final ServiceProvider<Number> parentServiceProvider = registerParentServiceProvider();
        final ServiceProvider<Integer> someServiceProvider = registerServiceProvider();
        when(someServiceProvider.isReady(any(ServiceManager.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return parentServiceProvider.isReady(any(ServiceManager.class));
            }
        });
        serviceManager.provide(Integer.class);
        verify(parentServiceProvider, times(1)).ready(any(ServiceManager.class));
    }

    // -- helper
    private ServiceProvider<Integer> registerServiceProvider() {
        ServiceProvider<Integer> serviceProvider = mock(ServiceProvider.class);
        when(serviceProvider.provide(notNull(ServiceManager.class))).thenReturn(anyInteger());
        serviceManager.registerProvider(Integer.class, serviceProvider);
        return serviceProvider;
    }

    private Integer anyInteger() {
        return 42;
    }

    private ServiceProvider<Number> registerParentServiceProvider() {
        final ServiceProvider<Number> parentServiceProvider = mock(ServiceProvider.class);
        when(parentServiceProvider.provide(any(ServiceManager.class))).thenReturn(anyNumber());
        serviceManager.registerProvider(Number.class, parentServiceProvider);
        when(parentServiceProvider.isReady(any(ServiceManager.class))).thenAnswer(new Answer<Boolean>() {
            boolean readyCalled = false;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                if (!readyCalled) {
                    parentServiceProvider.ready(any(ServiceManager.class));
                    readyCalled = true;
                }
                return readyCalled;
            }
        });
        return parentServiceProvider;
    }

    private Number anyNumber() {
        return null;
    }
}
