package org.rosuda.util.r;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.ShellContext;
import org.rosuda.util.r.impl.MockFileRStarter;
import org.rosuda.util.r.impl.MockStarterFactory;
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

    private File tempFile;
    private RStarterFactory rStarterFactory;
    private RStartContext rStartContext;
    private ProcessService<IRConnection> service;
    private IConnectionFactory connectionFactory;
    private IRConnection mockedIRConnection;

    private Runtime runtime;

    private static class EmptyTestShellContext extends ShellContext {
        @Override
        public String getEnvironmentVariable(String propertyName) {
            return null;
        }
    }

    @Before
    public void setUp() throws IOException {
        rStarterFactory = new RStarterFactory();
        rStartContext = new RStartContext();
        rStartContext.setShellContext(new EmptyTestShellContext());
        runtime = mock(Runtime.class);
        final Process process = mock(Process.class);
        when(process.getErrorStream()).thenReturn(mock(InputStream.class));
        when(process.getInputStream()).thenReturn(mock(InputStream.class));
        when(runtime.exec((String[]) any())).thenReturn(process);
        rStartContext.setRuntime(runtime);
        mockedIRConnection = mock(IRConnection.class);
    }

    @After
    public void cleanUp() {
        if (tempFile != null) {
            tempFile.delete();
        }
    }

    @Test
    public void usesAvailableRConnection() throws IOException {
        withAvailableIRConnection();
        service.start();
        verify(runtime, never()).exec((String[]) any());
        assertEquals(RUNSTATE.RUNNING, service.getRunState());
    }

    @Test
    public void startsProcessWhenNoIRConnectionIsAvailableAndFileLocationsAreEmpty() throws Exception {
        withMockStarterFactory();
        ensureRFileCanBeFound();
        errorWhenAcquiringIRConnection(new RServerException("any", "any"));
        service.start();
        verify(runtime, atLeast(1)).exec((String[]) any());
        assertEquals(RUNSTATE.RUNNING, service.getRunState());
    }

    @Test
    public void startsProcessWhenNoIRConnectionIsAvailable() throws Exception {
        errorWhenAcquiringIRConnection(new RServerException("any", "any"));
        ensureRFileCanBeFound();
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
        verify(connectionFactory, times(1)).shutdown(notNull(Properties.class));
    }

    // -- helper

    private void withAvailableIRConnection() {
        connectionFactory = mock(IConnectionFactory.class);
        when(connectionFactory.createRConnection(any(Properties.class))).thenReturn(mockedIRConnection);
        rStarterFactory.setContext(rStartContext);
        rStartContext.setConnectionFactory(connectionFactory);
        this.service = rStarterFactory.createService();
    }

    private void errorWhenAcquiringIRConnection(final Throwable throwable) {
        connectionFactory = mock(IConnectionFactory.class);
        when(connectionFactory.createRConnection(any(Properties.class))).thenThrow(throwable).thenReturn(mockedIRConnection);
        rStarterFactory.setContext(rStartContext);
        rStartContext.setConnectionFactory(connectionFactory);
        this.service = rStarterFactory.createService();
    }

    private void withMockStarterFactory() {
        MockStarterFactory mockStarterFactory = new MockStarterFactory();
        mockStarterFactory.setStarter(new MockFileRStarter(mockStarterFactory.getRunstateHolder(), rStartContext));
        rStarterFactory = mockStarterFactory;
    }

    private void ensureRFileCanBeFound() throws IOException {
        tempFile = new File("R");
        tempFile.createNewFile();
    }
}
