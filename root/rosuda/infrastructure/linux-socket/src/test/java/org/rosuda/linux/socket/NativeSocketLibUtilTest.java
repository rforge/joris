package org.rosuda.linux.socket;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assume.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.newsclub.net.unix.AFUNIXSocket;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ShellContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeSocketLibUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeSocketLibUtilTest.class);
    private NativeSocketLibUtil nativeSocketLibUtil;
    private TestShellContext shellContext;

    private static class TestShellContext extends ShellContext {

        private Map<String, String> properties = new HashMap<String, String>();

        public void setProperty(String key, String value) {
            this.properties.put(key, value);
        }

        @Override
        public String getEnvironmentVariable(String propertyName) {
            if (properties.containsKey(propertyName)) {
                return properties.get(propertyName);
            }
            return super.getEnvironmentVariable(propertyName);
        }
    }

    @Before
    public void setUp() {
        nativeSocketLibUtil = new NativeSocketLibUtil();
        shellContext = new TestShellContext();
        nativeSocketLibUtil.setShellContext(shellContext);
    }

    @After
    public void tearDown() {
        nativeSocketLibUtil.resetCache();
//TODO test lib props before/after (from system!)
    }

    @Test
    @SuppressWarnings("unchecked")
    public void theMagicPathHasBeenSetIfOSIsNotWindows() {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        String anystring = "anystring";
        System.setProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH, anystring);
        nativeSocketLibUtil.enableDomainSockets();
        assertThat(System.getProperties(), both(anystring));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void thePathLoadedHasBeenSetIfOSIsNotWindows() {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        String anystring = "anystring";
        System.setProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH, anystring);
        nativeSocketLibUtil.enableDomainSockets();
        assertThat(System.getProperties(), both(NativeSocketLibUtil.PROP_LIBRARY_LOADED));
    }

    @Test
    public void whenTheMagicPathFromSystemPropertiesExistsItIsPreserved() throws Exception {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        String path = prepareSocketFolder();

        nativeSocketLibUtil.enableDomainSockets();

        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), sameInstance(path));
    }

    @Test
    public void systemPropertiesAreSetWhenEnablingDomainSockets() throws IOException {
        nativeSocketLibUtil.resetCache();
        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));
        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));

        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));
        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), nullValue(String.class));

        prepareSocketFolder();
        nativeSocketLibUtil.enableDomainSockets();

        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), notNullValue(String.class));
        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), notNullValue(String.class));
    }

    //currently unsupported, should work after resetCache is properly implemented
    @Ignore
    @Test
    public void temporaryFilesAreDeletedWhenCleanCacheIsCalled() throws IOException {
        prepareSocketFolder();
        nativeSocketLibUtil.enableDomainSockets();        
        String libraryPath = System.getProperty(NativeSocketLibUtil.PROP_LIBRARY_LOADED);
        LOGGER.info("libraryPath-file = "+libraryPath);
        final File file = new File(libraryPath);
        assertTrue("file \"" + file.getAbsolutePath() + "\" has not been created.", file.exists());

        nativeSocketLibUtil.resetCache();

        assertFalse(file.exists());
    }
    
    //remove later
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
        shellContext.setProperty(NativeSocketLibUtil.NATIVE_LIB_PATH, path);
        return path;
    }
}