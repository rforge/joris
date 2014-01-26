package org.rosuda.util.service;

class ServiceFactory {

    protected <TYPE> TYPE init(final ServiceManager serviceManager, final ServiceProvider<TYPE> serviceProvider) {
        if (!serviceProvider.isReady(serviceManager)) {
            serviceProvider.ready(serviceManager);
        }
        return serviceProvider.provide(serviceManager);
    }
}
