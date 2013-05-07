package org.rosuda.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.cfg.IRConnectionConfig;
import org.rosuda.irconnect.cfg.IRConnectionConfigImpl;
import org.rosuda.irconnect.cfg.IRConnectionConfigStep;
import org.rosuda.irconnect.cfg.LibraryInstallationStep;
import org.rosuda.irconnect.cfg.LoadLibraryStep;
import org.rosuda.irconnect.mgr.IRConnectionMgrImpl;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.ProcessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath*:spring/r-service.spring.xml")
public class SpringConfiguration {

    @Bean
    public IRConnection managedIRConnection(IRConnectionConfig configuration, @Qualifier("rStarterProcess") ProcessService<IRConnection> rStarterService) {
	final IRConnectionMgrImpl irConnectionMgrImpl = new IRConnectionMgrImpl();
	irConnectionMgrImpl.setService(rStarterService);
	irConnectionMgrImpl.setFactory(new REngineConnectionFactory());
	return irConnectionMgrImpl.getIRConnection(configuration);
    }

    // -- helper
    
    private List<String> requiredInstallLibraries() {
	return Arrays.asList("TIMP");
    }
    
    private List<String> requiredLibraries() {
	return Arrays.asList("TIMP", "MASS");
    }

    @Bean 
    IRConnectionConfig connectionConfiguration() {
	final IRConnectionConfigImpl irConnectionConfigImpl = new IRConnectionConfigImpl();
	final List<IRConnectionConfigStep> configurationSteps = new ArrayList<IRConnectionConfigStep>();
	for (final String libraryName : requiredInstallLibraries()) {
	    final LibraryInstallationStep installLib = new LibraryInstallationStep();
	    installLib.setLibrary(libraryName);
	    configurationSteps.add(installLib);
	}
	for (final String libraryName : requiredLibraries()) {
	    final LoadLibraryStep loadLib = new LoadLibraryStep();
	    loadLib.setLibrary(libraryName);
	    configurationSteps.add(loadLib);
	}
	irConnectionConfigImpl.setSteps(configurationSteps );
	return irConnectionConfigImpl;
    }
}
