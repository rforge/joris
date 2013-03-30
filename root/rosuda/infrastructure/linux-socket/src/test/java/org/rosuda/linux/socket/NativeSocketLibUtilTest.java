package org.rosuda.linux.socket;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ShellContext;

public class NativeSocketLibUtilTest {

    private NativeSocketLibUtil nativeSocketLibUtil;
    private TestShellContext shellContext;

    private static class TestShellContext extends ShellContext {

        private Map<String, String> properties = new HashMap<String, String>();

        public void setProperty(String key, String value) {
            this.properties.put(key, value);
        }

        @Override
        public String getProperty(String propertyName) {
            if (properties.containsKey(propertyName)) {
                return properties.get(propertyName);
            }
            return super.getProperty(propertyName);
        }
    }

    @Before
    public void setUp() {
        nativeSocketLibUtil = new NativeSocketLibUtil();
        shellContext = new TestShellContext();
        nativeSocketLibUtil.setShellContext(shellContext);
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Matcher both(String input) {
        Matcher equalToInput = equalTo(input);
        Matcher notEqualToInput = not(equalToInput);
        Matcher notNullValue = notNullValue(String.class);
        return Matchers.both(notEqualToInput).and(notNullValue);
    }

    @Test
    public void whenTheMagicPathFromSystemPropertiesExistsItIsPreserved() throws Exception {
        if (OS.isWindows()) {
            assertTrue("pass smilingly", true);
            return;
        }
        File testFile = nativeSocketLibUtil.createTempFolder();

        testFile.mkdirs();
        String path = testFile.getAbsolutePath();
        shellContext.setProperty(NativeSocketLibUtil.NATIVE_LIB_PATH, path);

        nativeSocketLibUtil.enableDomainSockets();

        assertThat(System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH), sameInstance(path));
    }
}