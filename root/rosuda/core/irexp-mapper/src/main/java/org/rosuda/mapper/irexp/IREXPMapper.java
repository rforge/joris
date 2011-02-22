package org.rosuda.mapper.irexp;

import java.util.HashMap;
import java.util.Map;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.type.Node;


public class IREXPMapper<T> extends AbstractGenericMapper<IREXP, T>{

	private final Map<Integer, AbstractGenericMapper<IREXP,T>> mappings = new HashMap<Integer, AbstractGenericMapper<IREXP,T>>();
	
	public IREXPMapper() {
		super(true); //this is a factory and just redericts to specialized subinstances
		mappings.put(IREXP.XT_BOOL, new BoolMapper<T>());
		mappings.put(IREXP.XT_DOUBLE, new DoubleMapper<T>());
		mappings.put(IREXP.XT_INT, new IntegerMapper<T>());
		mappings.put(IREXP.XT_NULL, new NullMapper<T>());
		mappings.put(IREXP.XT_STR, new StringMapper<T>());
		mappings.put(IREXP.XT_SYM, new SymbolMapper<T>());
		
		mappings.put(IREXP.XT_FACTOR, new FactorMapper<T>());
		
		mappings.put(IREXP.XT_ARRAY_BOOL, new BoolArrayMapper<T>());
		mappings.put(IREXP.XT_ARRAY_BOOL_UA, new BoolArrayMapper<T>());		
		mappings.put(IREXP.XT_ARRAY_DOUBLE, new DoubleArrayMapper<T>());
		mappings.put(IREXP.XT_ARRAY_INT, new IntegerArrayMapper<T>());
		mappings.put(IREXP.XT_ARRAY_STR, new StringArrayMapper<T>());
		
		mappings.put(IREXP.XT_VECTOR, new VectorMapper<T>(this));
		mappings.put(IREXP.XT_MAP, new MapMapper<T>(this));
		
		mappings.put(IREXP.XT_MATRIX, new MatrixMapper<T>(this));
		
		//? does any of these types feature an effect to be mapped ?
		mappings.put(IREXP.XT_LANG, new IterableMapper<T>(this));
		mappings.put(IREXP.XT_LIST, new IterableMapper<T>(this));	
	}
	
	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {		
	//delegate to own factory according to type:
		final AbstractGenericMapper<IREXP, T> internalHandler = mappings.get(source.getType());
		if (internalHandler != null) {
			internalHandler.map(source, parent, trace);
		} else
			throw new UnsupportedOperationException("unknown type "+source.getType());
	}

	public ObjectTransformationHandler<T> createInstance() {
		final ObjectTransformationHandler<T> rootHander = new ObjectTransformationHandler<T>();
		rootHander.registerMapper(IREXP.class, new IREXPMapper<T>());
		return rootHander;
	}
	
}
