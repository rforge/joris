package org.rosuda.linux.socket;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.newsclub.net.unix.AFUNIXSocket;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.TestShellContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeSocketLibUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeSocketLibUtilTest.class);
    private NativeSocketLibUtil nativeSocketLibUtil;
    private TestShellContext shellContext;

    @Before
    public void setUp() {
        nativeSocketLibUtil = new NativeSocketLibUtil();
        shellContext = new TestShellContext();
        shellContext.setSystemProperty("os.name", "mock-os");
        shellContext.setSystemProperty("os.arch", "Z-80");
        nativeSocketLibUtil.setShellContext(shellContext);
    }

    @After
    public void tearDown() {
        nativeSocketLibUtil.resetCache();
        // TODO test lib props before/after (from system!)
    }

    @Test
    @SuppressWarnings("unchecked")
    public void theMagicPathHasBeenSetIfOSIsNotWindows() {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        String anystring = "anystring";
        shellContext.setEnvironmentProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH, anystring);
        nativeSocketLibUtil.enableDomainSockets();
        assertThat(shellContext.getEnvironment(), both(anystring));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void thePathLoadedHasBeenSetIfOSIsNotWindows() {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        String anystring = "anystring";
        shellContext.setEnvironmentProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH, anystring);
        nativeSocketLibUtil.enableDomainSockets();
        assertThat(shellContext.getEnvironment(), both(NativeSocketLibUtil.PROP_LIBRARY_LOADED));
    }

    @Test
    public void whenTheMagicPathFromSystemPropertiesExistsItIsPreserved() throws Exception {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        String path = prepareSocketFolder();

        nativeSocketLibUtil.enableDomainSockets();

        assertThat(shellContext.getSystemProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), sameInstance(path));
    }

    @Test
    public void systemPropertiesAreSetWhenEnablingDomainSockets() throws IOException {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        nativeSocketLibUtil.resetCache();
        assertThat(shellContext.getSystemProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));
        assertThat(shellContext.getSystemProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));

        assertThat(shellContext.getSystemProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));
        assertThat(shellContext.getSystemProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));

        prepareSocketFolder();
        nativeSocketLibUtil.enableDomainSockets();

        assertThat(shellContext.getSystemProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), notNullValue(String.class));
        assertThat(shellContext.getSystemProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), notNullValue(String.class));
    }

    // currently unsupported, should work after resetCache is properly
    // implemented
    @Ignore
    @Test
    public void temporaryFilesAreDeletedWhenCleanCacheIsCalled() throws IOException {
        prepareSocketFolder();
        nativeSocketLibUtil.enableDomainSockets();
        String libraryPath = shellContext.getSystemProperty(NativeSocketLibUtil.PROP_LIBRARY_LOADED);
        LOGGER.info("libraryPath-file = " + libraryPath);
        final File file = new File(libraryPath);
        assertTrue("file \"" + file.getAbsolutePath() + "\" has not been created.", file.exists());

        nativeSocketLibUtil.resetCache();

        assertFalse(file.exists());
    }

    // remove later
    @Ignore
    @Test
    public void nativeLibCanBeLoadedOrYourEnvironmentIsWrong() {
        nativeSocketLibUtil.enableDomainSockets();
        assumeTrue(AFUNIXSocket.isSupported());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Matcher both(String input) {
        Matcher equalToInput = equalTo(input);
        Matcher notEqualToInput = not(equalToInput);
        Matcher notNullValue = notNullValue(String.class);
        return Matchers.both(notEqualToInput).and(notNullValue);
    }

    private String prepareSocketFolder() throws IOException {
        File testFile = nativeSocketLibUtil.createTempFolder();
        testFile.mkdirs();
        String path = testFile.getAbsolutePath();
        shellContext.setEnvironmentProperty(NativeSocketLibUtil.NATIVE_LIB_PATH, path);
        return path;
    }
}