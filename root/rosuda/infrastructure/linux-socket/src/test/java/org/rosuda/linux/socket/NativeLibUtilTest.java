package org.rosuda.linux.socket;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.linux.socket.NativeLibUtil.ClassLoaderLibInspector;

public class NativeLibUtilTest {

    private ClassLoaderLibInspector inspector;

    @Before
    public void setUp() {
        inspector = new ClassLoaderLibInspector();
    }

    @Test
    public void notNullVectorIsFoundOnSystemClassLoader() {
        assertThat(inspector.getLoadedLibraries(ClassLoader.getSystemClassLoader()), notNullValue(Collection.class));
    }

    @Test
    public void unknownLibIsNotFound() {
        assertThat(NativeLibUtil.isLibraryAlreadyLoaded("someUnknownLib"), equalTo(false));
    }
}
