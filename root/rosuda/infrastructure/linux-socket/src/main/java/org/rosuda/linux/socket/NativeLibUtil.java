package org.rosuda.linux.socket;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeLibUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLibUtil.class);
    private static ClassLoaderLibInspector inspector;

    private static ClassLoaderLibInspector getInspector() {
        if (inspector == null) {
            inspector = new ClassLoaderLibInspector();
        }
        return inspector;
    }

    public static boolean isLibraryAlreadyLoaded(String libName) {
        final ClassLoaderLibInspector inspector = getInspector();
        final Set<String> libs = new TreeSet<String>();
        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Set<ClassLoader> usedLoaders = new HashSet<ClassLoader>();
        if (classLoader == null) {
            classLoader = systemClassLoader;
        }
        do {
            if (classLoader == null) {
                break;
            }
            libs.addAll(inspector.getLoadedLibraries(classLoader));
            usedLoaders.add(classLoader);
        } while ((classLoader = classLoader.getParent()) == null);
        if (!usedLoaders.contains(systemClassLoader)) {
            libs.addAll(inspector.getLoadedLibraries(classLoader));
        }
        return libs.contains(libName);
    }

    protected static class ClassLoaderLibInspector {

        private static final Field LOADED_LIBRARY_NAMES;

        static {
            try {
                LOADED_LIBRARY_NAMES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
                LOADED_LIBRARY_NAMES.setAccessible(true);
            } catch (final Exception e) {
                throw new RuntimeException("could not access class-loader field loadedLibraryNames", e);
            }
        }

        @SuppressWarnings("unchecked")
        Collection<String> getLoadedLibraries(final ClassLoader loader) {
            LOGGER.info("gettling loaded libs from ClassLoader " + loader.getClass().getSimpleName());
            try {
                return (Collection<String>) LOADED_LIBRARY_NAMES.get(loader);
            } catch (Exception e) {
                throw new RuntimeException("could not acces class-loader field loadedLibraryNames", e);
            }
        }

    }

}
