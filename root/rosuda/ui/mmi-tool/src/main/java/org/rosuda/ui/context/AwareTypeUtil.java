package org.rosuda.ui.context;

import java.lang.reflect.ParameterizedType;

public class AwareTypeUtil {

  
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getType(final Aware<T> aware) {
	return (Class<T>) ((ParameterizedType)aware.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }
}
