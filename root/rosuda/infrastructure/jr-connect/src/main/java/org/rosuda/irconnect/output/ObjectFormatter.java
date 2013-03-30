package org.rosuda.irconnect.output;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectFormatter extends AbstractObjectFormatter implements TypedObjectFormatter<Object> {

    private final String JMOCKIT_PROXY_PREFIX = "$Impl_";

    private ObjectArrayFormatter<Object> arrayFormatter = new ObjectArrayFormatter<Object>();
    private final Map<Class<?>, TypedObjectFormatter<?>> formatters = initFormatters();

    private Map<Class<?>, TypedObjectFormatter<?>> initFormatters() {
        Map<Class<?>, TypedObjectFormatter<?>> map = new HashMap<Class<?>, TypedObjectFormatter<?>>();
        // initialization is possible but not required
        // map.put(IREXP.class, new IREXPFormatterImpl(this));
        // map.put(IRBool.class, new IRBoolFormatter());
        // map.put(IRFactor.class, new IRFactorFormatter(this));
        // map.put(IRList.class, new IRListFormatter(this));
        // map.put(Double.class, new DoubleFormatter());
        // map.put(Number.class, new NumberFormatter());
        // map.put(String.class, new StringFormatter());
        return Collections.synchronizedMap(map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String format(final Object value) {
        if (value == null)
            return getReplacement(null);
        final Class<?> objClass = value.getClass();
        if (useArrayWrapper(objClass)) {
            return formatArray(wrapObjectForFormatter(objClass, value));
        } else {
            @SuppressWarnings("rawtypes")
            final TypedObjectFormatter formatter = findFormatterForObject(objClass);
            if (formatter != null) {
                try {
                    return formatter.format(value);
                } catch (final Exception x) {
                    LOGGER.error("failed to format " + value, x);
                }
            }
        }
        LOGGER.warn("no Formatter found for objClass = " + objClass);
        return null;
    }

    private boolean useArrayWrapper(final Class<?> objClass) {
        return (objClass.isArray() || Collection.class.isAssignableFrom(objClass));
    }

    @SuppressWarnings("unchecked")
    private ArrayWrapper<Object> wrapObjectForFormatter(final Class<?> objClass, final Object value) {
        if (objClass.isArray()) {
            return new ArrayClassArrayWrapper<Object>(value);
        } else if (Collection.class.isAssignableFrom(objClass)) {
            return new CollectionClassArrayWrapper<Object>(Object.class, (Collection<Object>) value);
        } else {
            return null;
        }

    }

    private TypedObjectFormatter<?> findFormatterForObject(final Class<?> objClass) {
        if (formatters.containsKey(objClass)) {
            LOGGER.debug("find formatter for " + objClass);
            return formatters.get(objClass);
        }
        // try to find a matching formatter:
        final Set<Class<?>> classFilter = new HashSet<Class<?>>();
        final Class<?> formatterClass = findFormatterClass(objClass, classFilter);
        if (formatterClass != null) {
            final TypedObjectFormatter<?> matchingFormatter = createFormatter(objClass, formatterClass);
            if (matchingFormatter != null) {
                formatters.put(objClass, matchingFormatter);
            }
            return matchingFormatter;
        }
        return null;
    }

    private TypedObjectFormatter<?> createFormatter(final Class<?> objClass, final Class<?> formatterClass) {
        Constructor<?> defaultConstructor = null;
        Constructor<?> objectFormatterConstructor = null;
        // check for empty constructor
        for (final Constructor<?> constructor : formatterClass.getConstructors()) {
            if (constructor.getParameterTypes() == null || constructor.getParameterTypes().length == 0)
                defaultConstructor = constructor;
            else if (constructor.getParameterTypes() != null && constructor.getParameterTypes().length == 1
                    && ObjectFormatter.class.equals(constructor.getParameterTypes()[0])) {
                objectFormatterConstructor = constructor;
            }
        }

        if (objectFormatterConstructor != null) {
            try {
                return (TypedObjectFormatter<?>) objectFormatterConstructor.newInstance(this);
            } catch (final Exception e) {
                LOGGER.error("error creating formatter instance", e);
            }
        } else if (defaultConstructor != null) {
            try {
                return (TypedObjectFormatter<?>) defaultConstructor.newInstance();
            } catch (final Exception e) {
                LOGGER.error("error creating formatter instance", e);
            }
        }
        return null;
    }

    private Class<?> findFormatterClass(final Class<?> aClassToFormat, final Set<Class<?>> classFilter) {
        Class<?> aMatchingClass = aClassToFormat;
        Class<?> formatterClass = null;
        while (!java.lang.Object.class.equals(aMatchingClass) && formatterClass == null) {
            formatterClass = findFormatterClassByInterfaces(aMatchingClass, classFilter);
            if (formatterClass == null && !classFilter.contains(aMatchingClass)) {
                classFilter.add(aMatchingClass);
                formatterClass = findFormatterClassFor(aMatchingClass);
            }
            aMatchingClass = aMatchingClass.getSuperclass();
        }
        return formatterClass;
    }

    private Class<?> findFormatterClassByInterfaces(final Class<?> aProxyClassToFormat, final Set<Class<?>> classFilter) {
        final List<Class<?>> interfaces = Arrays.asList(aProxyClassToFormat.getInterfaces());
        Class<?> formatterClass = null;
        final Iterator<Class<?>> interfaceIterator = interfaces.iterator();
        while (formatterClass == null && interfaceIterator.hasNext()) {
            final Class<?> interfaceClass = interfaceIterator.next();
            if (!classFilter.contains(interfaceClass)) {
                classFilter.add(interfaceClass);
                formatterClass = findFormatterClassFor(interfaceClass);
            }
        }
        return formatterClass;
    }

    private Class<?> findFormatterClassFor(final Class<?> anyClass) {
        String simpleClassName = anyClass.getSimpleName();
        if (simpleClassName.startsWith(JMOCKIT_PROXY_PREFIX))
            simpleClassName = simpleClassName.substring(JMOCKIT_PROXY_PREFIX.length());
        final String packageName = this.getClass().getPackage().toString().substring("package ".length());
        final StringBuilder classNameBuilder = new StringBuilder(packageName).append(".").append(simpleClassName).append("Formatter");
        LOGGER.debug("try class \"" + classNameBuilder + "\"");
        try {
            return Class.forName(classNameBuilder.toString());
        } catch (final ClassNotFoundException e) {
            LOGGER.debug("unknown class '" + classNameBuilder + "'");
            return null;
        }
    }

    @SuppressWarnings("static-access")
    private String formatArray(final ArrayWrapper<?> array) {
        return arrayFormatter.format(toArray(array), this);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final ArrayWrapper<T> array) {
        if (array.getSize() == 0) {
            return (T[]) Collections.emptySet().toArray();
        }
        Object instance = array.get(0);
        if (instance == null) {
            for (int i = 1; i < array.getSize(); i++) {
                if (instance == null) {
                    instance = array.get(i);
                }
            }
        }
        if (instance == null) {
            return (T[]) Array.newInstance(Object.class, array.getSize());
        }
        final Class<T> arrayTypeClass = (Class<T>) instance.getClass();
        final T[] objects = (T[]) Array.newInstance(arrayTypeClass, array.getSize());
        for (int i = 0; i < objects.length; i++) {
            objects[i] = array.get(i);
        }
        return objects;
    }

    public interface ArrayWrapper<T> {
        int getSize();

        T get(int pos);
    }

    public static final class ArrayClassArrayWrapper<T> implements ArrayWrapper<T> {
        final T value;

        ArrayClassArrayWrapper(final T value) {
            this.value = value;
        }

        @Override
        public int getSize() {
            return Array.getLength(value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T get(final int pos) {
            return (T) Array.get(value, pos);
        }
    }

    public static class ArrayArrayWrapper<T> implements ArrayWrapper<T> {
        final T[] array;

        public ArrayArrayWrapper(final T[] array) {
            this.array = array;
        }

        @Override
        public int getSize() {
            return array.length;
        }

        @Override
        public T get(final int pos) {
            return array[pos];
        }

    }

    public static final class CollectionClassArrayWrapper<T> extends ArrayArrayWrapper<T> implements ArrayWrapper<T> {

        @SuppressWarnings("unchecked")
        public CollectionClassArrayWrapper(final Class<T> forClass, final Collection<T> collection) {
            super(collection.toArray((T[]) Array.newInstance(forClass, collection.size())));
            LOGGER.debug("wrapped Collection<" + forClass + ">:" + collection);
        }
    }

}
