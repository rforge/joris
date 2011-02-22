package org.rosuda.util.process;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * base factory class to create start and stoppable services
 * @author ralfseger
 *
 * @param <T>
 */
public abstract class ProcessFactory<T> {

	static final Class<?>[] serviceInterfaces = new Class<?>[] {
			ProcessStarter.class, ProcessStopper.class, HasRunState.class,
			ProcessService.class };

	abstract protected ProcessStarter<T> createStarter();

	abstract protected ProcessStopper<T> createStopper();

	abstract protected HasRunState<T> createHasRunState();

	@SuppressWarnings("unchecked")
	public ProcessService<T> createService() {
		return (ProcessService<T>) Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), serviceInterfaces, new ProcessProxy());
	}

	class ProcessProxy implements InvocationHandler {

		private final Object[] delegates;

		protected ProcessProxy() {
			this.delegates = new Object[] {
					ProcessFactory.this.createStarter(),
					ProcessFactory.this.createStopper(),
					ProcessFactory.this.createHasRunState() };
		}

		@Override
		public Object invoke(final Object proxy, final Method method,
				final Object[] args) throws Throwable {
			final Class<?> declaringClass = method.getDeclaringClass();

			for (int i = 0; i < serviceInterfaces.length; i++) {
				if (declaringClass.isAssignableFrom(serviceInterfaces[i])) {
					try {
						return method.invoke(delegates[i], args);
					} catch (final InvocationTargetException e) {
						throw e.getTargetException();
					}
				}
			}
			throw new UnsupportedOperationException("cannot execute method "
					+ method);
		}
	}
}
