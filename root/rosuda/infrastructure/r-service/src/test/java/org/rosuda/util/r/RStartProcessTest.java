package org.rosuda.util.r;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.r.impl.RStartContext;
import org.rosuda.util.r.impl.RStarterFactory;

/**
 * this tests the start/stop of an REngine-Server-Process the @Ignore test cases
 * work but block maven build
 * 
 * @author ralfseger
 * 
 */
public class RStartProcessTest {

    private RStartContext rStartContext;
    private ProcessService<IRConnection> service;
    private IConnectionFactory connectionFactory;
    private IRConnection mockedIRConnection;

    private Runtime runtime;
   
    @Before
    public void setUp() throws IOException {
	rStartContext = new RStartContext();
	runtime = mock(Runtime.class);
	final Process process = mock(Process.class);
	when(process.getErrorStream()).thenReturn(mock(InputStream.class));
	when(process.getInputStream()).thenReturn(mock(InputStream.class));
	when(runtime.exec((String[]) any())).thenReturn(process);
	rStartContext.setRuntime(runtime);
	mockedIRConnection = mock(IRConnection.class);
    }

    @Test
    public void usesAvailableRConnection() throws IOException {
	withAvailableIRConnection();
	service.start();
	verify(runtime, never()).exec((String[]) any());
	assertEquals(RUNSTATE.RUNNING, service.getRunState());
    }

    @Test
    public void startsProcessWhenNoIRConnectionIsAvailable() throws Exception {
	errorWhenAcquiringIRConnection(new RServerException("any", "any"));
	service.start();
	verify(runtime, atLeast(1)).exec((String[]) any());
	assertEquals(RUNSTATE.RUNNING, service.getRunState());
    }

    @Test
    public void testStopProcess() {
	withAvailableIRConnection();
	service.start();
	service.stop();
	Assert.assertEquals(RUNSTATE.TERMINATED, service.getRunState());
	verify(connectionFactory, times(1)).shutdown();
    }

    // -- helper

    private void withAvailableIRConnection() {
	final RStarterFactory factory = new RStarterFactory();
	connectionFactory = mock(IConnectionFactory.class);
	when(connectionFactory.createRConnection(any(Properties.class))).thenReturn(mockedIRConnection);
	factory.setContext(rStartContext);
	rStartContext.setConnectionFactory(connectionFactory);
	this.service = factory.createService();
    }
    
    private void errorWhenAcquiringIRConnection(final Throwable throwable) {
	final RStarterFactory factory = new RStarterFactory();
	connectionFactory = mock(IConnectionFactory.class);
	when(connectionFactory.createRConnection(any(Properties.class))).thenThrow(throwable).thenReturn(mockedIRConnection);
	factory.setContext(rStartContext);
	rStartContext.setConnectionFactory(connectionFactory);
	this.service = factory.createService();
    }
}
