/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rosuda.irconnect.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.rosuda.irconnect.ARConnection;
import org.rosuda.irconnect.IJava2RConnection;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IRConnectionEvent;
import org.rosuda.irconnect.ITwoWayConnection;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ralf
 */
public class RConnectionProxy implements InvocationHandler {

    private final static Class<?>[] interfaces = new Class[] { IRConnection.class, IJava2RConnection.class, ITwoWayConnection.class };
    private final Object[] delegates;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;
    private static Method closeMethod;
    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode");
            equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
            toStringMethod = Object.class.getMethod("toString");
            closeMethod = IRConnection.class.getMethod("close");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    private static final Set<Method> evalMethods = getEvalMethods();
    private static final Set<Method> setMethods = getSetMethods();

    private static final Set<Method> getEvalMethods() {
        final Set<Method> methods = new HashSet<Method>();
        try {
            methods.add(IRConnection.class.getMethod("voidEval", new Class[] { String.class }));
            methods.add(IRConnection.class.getMethod("eval", new Class[] { String.class }));
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
        return methods;
    }

    private static final Set<Method> getSetMethods() {
        final Set<Method> methods = new HashSet<Method>();
        try {
            // methods.add(IJava2RConnection.class.getMethod("assign", new
            // Class[] { String.class , byte[].class}));
            methods.add(IJava2RConnection.class.getMethod("assign", new Class[] { String.class, int[].class }));
            methods.add(IJava2RConnection.class.getMethod("assign", new Class[] { String.class, double[].class }));
            methods.add(IJava2RConnection.class.getMethod("assign", new Class[] { String.class, String.class }));
            methods.add(IJava2RConnection.class.getMethod("assign", new Class[] { String.class, String[].class }));
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
        return methods;
    }

    public static ITwoWayConnection createProxy(final IRConnection irconnection, final IJava2RConnection irtransfer) {
        return (ITwoWayConnection) Proxy.newProxyInstance(irconnection.getClass().getClassLoader(), interfaces, new RConnectionProxy(
                irconnection, irtransfer));
    }

    private RConnectionProxy(final IRConnection irconnection, final IJava2RConnection irtransfer) {
        delegates = new Object[] { irconnection, irtransfer };
    }

    public Object invoke(final Object proxy, final Method m, final Object[] args) throws Throwable {
        Class<?> declaringClass = m.getDeclaringClass();

        LoggerFactory.getLogger(RConnectionProxy.class).info("invoking " + m);
        if (declaringClass == Object.class) {
            if (m.equals(hashCodeMethod)) {
                return proxyHashCode(proxy);
            } else if (m.equals(equalsMethod)) {
                return proxyEquals(proxy, args[0]);
            } else if (m.equals(toStringMethod)) {
                return proxyToString(proxy);
            } else {
                throw new InternalError("unexpected Object method dispatched: " + m);
            }
        } else {
            if (closeMethod.equals(m)) {
                for (Object delegate : delegates) {
                    if (delegate instanceof ARConnection) {
                        ((ARConnection) delegate)
                                .notifyListeners(new IRConnectionEvent.Event(IRConnectionEvent.Type.CLOSE, null, delegate));
                    }
                }
            }
            if (evalMethods.contains(m) || setMethods.contains(m)) {
                for (int i = 0; i < delegates.length; i++) {
                    if (delegates[i] instanceof ARConnection) {
                        final ARConnection notifyCon = (ARConnection) delegates[i];
                        // check i fmethod from SET => assign is called or eval
                        if (setMethods.contains(m)) {
                            final Object target = args[1];
                            final StringBuffer messageBuffer = new StringBuffer();
                            if (target.getClass().isArray()) {
                                final int arrayLength = Array.getLength(target);
                                for (int ai = 0; ai < arrayLength; ai++) {
                                    messageBuffer.append(Array.get(target, ai));
                                    if (ai < arrayLength) {
                                        messageBuffer.append(",");
                                    }
                                }
                            } else {
                                messageBuffer.append(target);
                            }
                            notifyCon.notifyListeners(new IRConnectionEvent.Event(IRConnectionEvent.Type.SET, messageBuffer.toString(),
                                    RConnectionProxy.class));
                        } else if (evalMethods.contains(m)) {
                            notifyCon.notifyListeners(new IRConnectionEvent.Event(IRConnectionEvent.Type.EVALUATE, args[0].toString(),
                                    RConnectionProxy.class));
                        }
                    }
                }
            }
            for (int i = 0; i < interfaces.length; i++) {
                if (declaringClass.isAssignableFrom(interfaces[i])) {
                    try {
                        return m.invoke(delegates[i], args);
                    } catch (final InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            }

            throw new UnsupportedOperationException("cannot execute method " + m);
        }
    }

    protected Integer proxyHashCode(final Object proxy) {
        return new Integer(System.identityHashCode(proxy));
    }

    protected Boolean proxyEquals(final Object proxy, final Object other) {
        return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
    }

    protected String proxyToString(final Object proxy) {
        return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
    }
}
