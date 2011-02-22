package org.rosuda.type;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


class NodeProxy<T> implements InvocationHandler{


	private final static Class<?>[] interfaces = new Class<?>[]{Node.class/*, Node.Builder.class*/};
	private final Node<T> delegate;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;
    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode");
            equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
            toStringMethod = Object.class.getMethod("toString");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

 	public static final Set<Method> onlyNodeMethods = new HashSet<Method>(Arrays.asList(Node.class.getMethods()));

 	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Node<?> createProxy(final Node<?> node) {
        return (Node<?>)
            Proxy.newProxyInstance(node.getClass().getClassLoader(),
                                                  interfaces,
                                                  new NodeProxy(node));
    }

    private NodeProxy(final Node<T> delegate) {
        this.delegate = delegate;
    }

 	public Object invoke(final Object proxy, final Method m, final Object[] args)
	throws Throwable
    {
	Class<?> declaringClass = m.getDeclaringClass();

	if (declaringClass == Object.class) {
	    if (m.equals(hashCodeMethod)) {
            return proxyHashCode(proxy);
	    } else if (m.equals(equalsMethod)) {
            return proxyEquals(proxy, args[0]);
	    } else if (m.equals(toStringMethod)) {
            return proxyToString(proxy);
	    } else {
		throw new InternalError(
		    "unexpected Object method dispatched: " + m);
	    }
	} else if (onlyNodeMethods.contains(m)) {
		for (int i = 0; i < interfaces.length; i++) {
			if (declaringClass.isAssignableFrom(interfaces[i])) {
			    try {
				return m.invoke(delegate, args);
			    } catch (final InvocationTargetException e) {
	                throw e.getTargetException();
			    }
			}
		}
	}
	    throw new UnsupportedOperationException("method forbidden by proxy "+m);
	}

    protected Integer proxyHashCode(final Object proxy) {
        return new Integer(System.identityHashCode(proxy));
    }

    protected Boolean proxyEquals(final Object proxy, final Object other) {
        return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
    }

    protected String proxyToString(final Object proxy) {
    	return delegate.toString();
        /*return proxy.getClass().getName() + '@' +
            Integer.toHexString(proxy.hashCode());*/
    }
}
