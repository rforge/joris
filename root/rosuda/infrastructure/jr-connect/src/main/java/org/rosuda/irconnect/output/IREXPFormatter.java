package org.rosuda.irconnect.output;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IREXP;

public class IREXPFormatter extends DelegetableObjectFormatter<IREXP>{

	private static final Log LOG = LogFactory.getLog(IREXPFormatter.class);
	private final Map<Integer, Method> conversionMethods;
	
	private static Method getMethod(final String methodName) {
		try {
			return IREXP.class.getMethod(methodName);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public IREXPFormatter(final ObjectFormatter formatter)
	{
		super(formatter);
		final Map<Integer,Method> map = new TreeMap<Integer, Method>();
		map.put(IREXP.XT_ARRAY_BOOL,getMethod("asBoolArray"));		
		map.put(IREXP.XT_ARRAY_BOOL_UA,getMethod("asBoolArray"));		
		map.put(IREXP.XT_ARRAY_DOUBLE,getMethod("asDoubleArray"));
		map.put(IREXP.XT_ARRAY_INT,getMethod("asIntArray"));		
		map.put(IREXP.XT_ARRAY_STR,getMethod("asStringArray"));		
		map.put(IREXP.XT_BOOL,getMethod("asBool"));		
		//TODO check how this behaves
		//map.put(IREXP.XT_CLOS,"ClosFormatter");		
		map.put(IREXP.XT_DOUBLE,getMethod("asDouble"));		
		map.put(IREXP.XT_FACTOR,getMethod("asFactor"));
		map.put(IREXP.XT_INT,getMethod("asInt"));		
		//TODO check how this behaves
		//map.put(IREXP.XT_LANG,"LangFormatter");		
		map.put(IREXP.XT_LIST,getMethod("asList"));		
		map.put(IREXP.XT_MAP,getMethod("asMap"));	
		map.put(IREXP.XT_MATRIX,getMethod("asMatrix"));
		//TODO check how this behaves
		//map.put(IREXP.XT_NULL,"NullFormatter");		
		map.put(IREXP.XT_STR,getMethod("asString"));		
		map.put(IREXP.XT_SYM,getMethod("asSymbol"));
		//TODO check how this behaves
		//map.put(IREXP.XT_UNKNOWN,"UnknownFormatter");		
		map.put(IREXP.XT_VECTOR,getMethod("asVector"));
		this.conversionMethods = Collections.unmodifiableMap(map);
	}
	
	@Override
	public String format(final IREXP obj) {
		final Method convertMethod = conversionMethods.get(obj.getType());
		if (convertMethod == null) {
			LOG.warn("no formatter available for IREXP.type="+obj.getType());
			for (Field typeField: IREXP.class.getFields()) {
				if (int.class.equals(typeField.getType())) {
					try {
						final int fieldValue = typeField.getInt(IREXP.class);
						if (obj.getType() == fieldValue) {
							final String formatPattern = getReplacement(UNKNOWN_TYPE);
							return MessageFormat.format(formatPattern, IREXP.class.getSimpleName()+"."+typeField.getName());
						}
					} catch (final Exception e) {
						LOG.error(e);
					}
				}
			}
			return null;
		}
		try {
			return objectFormatter.format(convertMethod.invoke(obj));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
 	}

}
