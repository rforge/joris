package org.rosuda.mapper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.logging.Logger;

import org.rosuda.type.Node;
import org.rosuda.type.Value;


class ReflectionMapper<T> extends AbstractGenericMapper<Object,T> {

	private static final Logger logger = Logger.getLogger(ReflectionMapper.class.getCanonicalName());
	
	private final ObjectTransformationHandler<T> transformationHandler;
	
	protected ReflectionMapper(final ObjectTransformationHandler<T> handler){
		this.transformationHandler = handler;
	}

	protected String getNodeName(final Object object) {
		if (object.getClass().isArray()) {
			if (Array.getLength(object)>0)
				return "ArrayOf"+getNodeName(Array.get(object,0));
			else 
				return "EmptyArray";
		} else {
			return object.getClass().getSimpleName();
		}
	}
	
	protected void handleMap(final Object source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {
		if (source == null) {
			logger.fine("skipping null object");
			return;
		}
		//check if source is an iterable type
		final Class<?> sourceClass = source.getClass();
		if (Proxy.isProxyClass(sourceClass)) {
			
		}
		if (Map.class.isAssignableFrom(sourceClass)) {
			final Node.Builder<T> mapContainer = parent.createChild("map");
			if (mapContainer == null)
				return;
			parent.add(mapContainer);
			for (final Map.Entry<?, ?> element : ((Map<?, ?>) source).entrySet()) {
				final Node.Builder<T> keyValue = mapContainer.createChild("entry");
				if (keyValue ==  null)
					continue;
				mapContainer.add(keyValue);
				final Node.Builder<T> key = keyValue.createChild("key");
				if (key == null)
					continue;
				keyValue.add(key);
				key.setValue(Value.newString(element.getKey().toString()));
				final Node.Builder<T> value = keyValue.createChild("value");
				if (value == null)
					continue;
				keyValue.add(value);
				transformationHandler.transform(element.getValue(), value, trace);
			}
		} else if (Iterable.class.isAssignableFrom(sourceClass)) {
			final Node.Builder<T> iterableContainer = parent.createChild("iterable");
			if (iterableContainer == null)
				return;
			parent.add(iterableContainer);
			for (final Object element : (Iterable<?>) source) {
				final Node.Builder<T> elementContainer = iterableContainer.createChild(getNodeName(element));
				if (elementContainer == null)
					continue;
				iterableContainer.add(elementContainer);
				transformationHandler.transform(element, elementContainer, trace);
			}
		} else if (sourceClass.isArray()) {
			final Node.Builder<T> arrayContainer = parent.createChild("array");
			if (arrayContainer == null)
				return;
			parent.add(arrayContainer);
			//createArrayOfNode
			final int elementCount = Array.getLength(source);
			if (elementCount>0) {
				for (int i=0;i<elementCount;i++) {
					final Object elementAt = Array.get(source, i);
					final Node.Builder<T> elementContainer = arrayContainer.createChild(getNodeName(elementAt));
					if (elementContainer == null)
						continue;
					arrayContainer.add(elementContainer);
					transformationHandler.transform(elementAt, elementContainer, trace);
				}
			}
			
			return;
		} else if (sourceClass.isEnum()) {
			System.out.println("found an Enum type");
		}
		//bean convention, find all public getX methods:
		final Method[] allMethods = source.getClass().getMethods();
		for (final Method method : allMethods) {
			if (method.getParameterTypes().length>0) //only void arguments are valid
				continue;
			if (!method.getName().startsWith("get"))
				continue;
			if ("getClass".equals(method.getName()))
				continue;
			//okay we have one here:
			final Node.Builder<T> container = parent.createChild(method.getName().substring(3));
			if (container == null)
				continue;
			parent.add(container);
			try {
				final Object value = method.invoke(source);
				 /*call handler*/
				transformationHandler.transform(value, container, trace);
			} catch (final Exception e) {
				final ByteArrayOutputStream bout = new ByteArrayOutputStream();
				e.printStackTrace(new PrintStream(bout));
				logger.severe(bout.toString());
			}
		}
	}
	
}
