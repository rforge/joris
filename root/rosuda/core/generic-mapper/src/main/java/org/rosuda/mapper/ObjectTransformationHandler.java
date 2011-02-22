package org.rosuda.mapper;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.rosuda.type.Node;

public class ObjectTransformationHandler<NodeType> {

	private static final Logger logger = Logger.getLogger(ObjectTransformationHandler.class.getCanonicalName());
	
	private final Map<Class<?>, AbstractGenericMapper<?,NodeType>> mappings = new HashMap<Class<?>, AbstractGenericMapper<?,NodeType>>();
	
	public ObjectTransformationHandler() {
		//static default handlers
		mappings.put(Number.class, new NumberMapper<NodeType>());
		mappings.put(String.class, new StringMapper<NodeType>());
		mappings.put(Boolean.class, new BoolMapper<NodeType>());
	}

	public final ReflectionMapper<NodeType> reflectionMapper = new ReflectionMapper<NodeType>(this);
	
	public void registerMapper(final Class<?> classObject, final AbstractGenericMapper<?, NodeType> mapper) {
		mappings.put(classObject, mapper);
	}
	
	public void transform(final Object source, final Node.Builder<NodeType> target) {
		transform(source, target, new MappedNodeTrace<NodeType>());
	}
	
	/**
	 * @param source
	 * @param parentNode
	 * @param traces needs to be given if a possibilty of loops exists
	 */
	public void transform(final Object source, final Node.Builder<NodeType> target, final MappedNodeTrace<NodeType> trace) {
		if (source != null) {
			if (!map(source.getClass(), source, target, trace)) {
				logger.warning("no mapping found for class "+source.getClass()+", using reflection");
				reflectionMapper.map(source, target, trace);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean map(final Class<?> mappedClass, final Object source, final Node.Builder<NodeType> node, final MappedNodeTrace<NodeType> trace) {
		if (source != null) {
			//find the right mapper and map
			final Class<?>[] interfaces = mappedClass.getInterfaces();
			boolean mappingfound = false;
	
			if (Proxy.isProxyClass(mappedClass)) {
				//special treatment - use interfaces only
				for (int i=0;i<interfaces.length;i++) {
					mappingfound = mappingfound || map(interfaces[i], source, node, trace);
				}
				return mappingfound;
			} else if (mappings.containsKey(mappedClass)){
				final AbstractGenericMapper<Object, NodeType> genericMapper = (AbstractGenericMapper<Object, NodeType>) mappings.get(mappedClass);
				genericMapper.map(source, node, trace);
				return true;
			} else if (interfaces != null && interfaces.length > 0) {
				//do mappings for each interface
				for (int i=0;i<interfaces.length;i++) {
					mappingfound = mappingfound || map(interfaces[i], source, node, trace);
				}
			}
			Class<?> superClass = mappedClass.getSuperclass();
			while (superClass != null) {
				mappingfound = mappingfound || map(superClass, source, node, trace);
				superClass = superClass.getSuperclass();
			}
			return mappingfound;
		}
		return false;
	}

}
