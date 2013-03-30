package org.rosuda.irconnect.output;

public class ObjectArrayFormatter<T> {

    public static <T> String format(final T[] objects, final TypedObjectFormatter<T> formatter) {
        final StringBuilder ret = new StringBuilder("[");
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                if (i > 0)
                    ret.append(", ");
                ret.append(formatter.format(objects[i]));
            }
        }
        return ret.append("]").toString();
    }
}
