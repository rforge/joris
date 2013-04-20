package org.rosuda.util.r.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServeOpts;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.ShellContext;
import org.rosuda.util.process.TestShellContext;

public class UnixStarterTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private UnixRStarter unixRStarter;
    private RStartContext context;
    private TestShellContext shellContext;

    @Before
    public void setUp() {
        RunStateHolder<IRConnection> runStateHolder = mock(RunStateHolder.class);
        context = new RStartContext();
        shellContext = new TestShellContext();
        shellContext.setOnlyInternalEnv(false);
        context.setShellContext(shellContext);
        this.unixRStarter = new UnixRStarter(runStateHolder, context);
    }

    @Test
    public void withStandardShellContextNoSocketIsUsed() {
        String executableRFile = "Rserve";
        assertThat(unixRStarter.getRuntimeArgs(executableRFile), not(hasItemInArray(containsString(RServeOpts.SOCKET.asRServeOption()))));
    }

    @Test
    public void givenTheShellContextProvidesASocketArgumentThisSocketIsUsed() {
        String socketValue = "/tmp/rservesocket";
        shellContext.setProperty(RServeOpts.SOCKET.getEnvironmentName(), socketValue);

        String executableRFile = "Rserve";
        assertThat(unixRStarter.getRuntimeArgs(executableRFile), hasItemInArray(containsString(RServeOpts.SOCKET.asRServeOption() + " "
                + socketValue + " ")));
    }

    @Test
    public void UnixRStarterChecksLocalPathEnvironmentForR() throws IOException {
        final File fakedRFile = new File(tempFolder.getRoot(), "R");
        fakedRFile.createNewFile();
        context.setShellContext(new ShellContext());
        ShellContext mockShellContext = mock(ShellContext.class);
        when(mockShellContext.getEnvironment()).thenReturn(Collections.singletonMap("PATH", fakedRFile.getParentFile().getAbsolutePath()));
        context.setShellContext(mockShellContext);

        final List<File> list = new ArrayList<File>();
        unixRStarter.initRFileLocations(list);

        assertThat(list, hasItem(fakedRFile));
    }
}
