package org.rosuda.mapper;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.rosuda.type.Node;

public class MappedNodeTrace<NodeType> {
	
	private static final Logger logger = Logger.getLogger(MappedNodeTrace.class.getCanonicalName());
	private final Map<Object, Node.Builder<NodeType>> mappings = new IdentityHashMap<Object, Node.Builder<NodeType>>();/*{

		private static final long serialVersionUID = 5900730300879099494L;
		
		@Override
		public boolean containsKey(final Object key) {
			logger.finest("containsKey ?"+key);
			for (final Object storedKey: super.keySet()) {
				if (key == storedKey)
					return true;
			}
			return false;
			//return super.containsKey(key);
		}
		
		@Override
		public Node put(final Object key, final Node node) {
			logger.finest("put("+key+","+node+")");
			return super.put(key, node);
		}
		
	};*/
	
	MappedNodeTrace() {}
	
	public final boolean willProduceLoop(final Object object, final Node.Builder<NodeType> node) {	
		if (mappings.containsKey(object)) {
			return true;
		} else {
			logger.fine("Object \""+object+"\" is rejected by loop condition");
			mappings.put(object, node);
			return false;
		}
		
	}
	
	public Node.Builder<NodeType> getLoopNode(final Object key) {
		return mappings.get(key);
	}
}
