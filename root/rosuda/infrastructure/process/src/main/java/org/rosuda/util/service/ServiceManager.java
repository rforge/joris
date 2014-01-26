package org.rosuda.util.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.rosuda.util.process.ShellContext;

public class ServiceManager {

    private Map<Class<?>, ServiceProvider<?>> providerMap = Collections.synchronizedMap(new HashMap<Class<?>, ServiceProvider<?>>());

    ShellContext shellContext = new ShellContext();

    public void setShellContext(ShellContext shellContext) {
        this.shellContext = shellContext;
    }

    public ShellContext getShellContext() {
        return shellContext;
    }

    public <TYPE> TYPE provide(final Class<TYPE> forClass) {
        return new ServiceFactory().init(this, findProvider(forClass));
    }

    public <TYPE> void registerProvider(Class<TYPE> forClass, ServiceProvider<TYPE> provider) {
        synchronized (providerMap) {
            providerMap.put(forClass, provider);
        }
    }

    protected <TYPE> ServiceProvider<TYPE> findProvider(final Class<TYPE> forClass) {
        ServiceProvider<TYPE> cachedProvider = null;
        synchronized (providerMap) {
            cachedProvider = (ServiceProvider<TYPE>) providerMap.get(forClass);
        }
        if (cachedProvider != null) {
            return cachedProvider;
        } else {
            ServiceProvider<TYPE> provider = createProvider(forClass);
            synchronized (providerMap) {
                providerMap.put(forClass, provider);
            }
            return provider;
        }
    }

    protected <TYPE> ServiceProvider<TYPE> createProvider(final Class<TYPE> forClass) {
        final String providerClassName = new StringBuilder("org.rosuda.util.service.").append(forClass.getSimpleName()).append("Provider")
                .toString();
        try {
            final Class<ServiceProvider<TYPE>> providerClass = (Class<ServiceProvider<TYPE>>) Class.forName(providerClassName);
            return providerClass.newInstance();
        } catch (Exception e) {
            throw new ServiceProviderException(e);
        }
    }
}
