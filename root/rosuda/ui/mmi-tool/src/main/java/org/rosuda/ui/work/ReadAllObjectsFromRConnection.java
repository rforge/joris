package org.rosuda.ui.work;

import java.util.Map;
import java.util.TreeMap;

import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMap;

import com.google.common.base.Function;

public class ReadAllObjectsFromRConnection implements Function<IRConnection, IREXP>{

	private static final ReadAllObjectsFromRConnection instance = new ReadAllObjectsFromRConnection();
	
	public static Function<IRConnection, IREXP> getInstance() {
		return instance;
	}
	
	@Override
	public IREXP apply(final IRConnection connection) {
		final IREXP rexp = connection.eval("objects()");
		final String[] objectNames = rexp.asStringArray();
		final Map<String, IREXP> evaluatedClosures = new TreeMap<String, IREXP>();
		for(final String objectName :objectNames) {
			evaluatedClosures.put(objectName, connection.eval(objectName));
		}
		final IRMap environmentMap = new IRMap() {
			@Override
			public String[] keys() {
				return objectNames;
			}
			@Override
			public IREXP at(final String string) {
				return evaluatedClosures.get(string);
			}
		};
		return new AREXP() {
			@Override
			public int getType() {
				return IREXP.XT_MAP;
			}
			@Override
			public IRMap asMap() {
				return environmentMap;
			}
		};
	}

}
