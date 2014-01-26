package org.rosuda.util.service;

public interface ServiceProvider<TYPE> {

    /**
     * represents the READY state of this service provider some ServiceProviders
     * may not have dependencies, thus always return true other might check
     * parent providers first
     * 
     * @param serviceManager
     *            TODO
     * 
     * @return
     */
    boolean isReady(ServiceManager serviceManager);

    /**
     * called if a service is not ready. guarantees isReady() or throw Exception
     * @param serviceManager TODO
     */
    void ready(ServiceManager serviceManager);

    /**
     * called after isReady() is true. returns the desired serviceobject
     * @param serviceManager TODO
     * 
     * @return
     */
    TYPE provide(ServiceManager serviceManager);

}
