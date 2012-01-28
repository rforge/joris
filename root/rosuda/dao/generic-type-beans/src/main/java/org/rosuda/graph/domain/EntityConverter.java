package org.rosuda.graph.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rosuda.type.Node;
import org.rosuda.type.Value;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * utility class to convert the "Entity" Graph to the generic representation and vice versa
 * @author ralfseger
 *
 */
public class EntityConverter {

	public static final Function<Node<?>, Graph> convertNodeToGraphEntity = new Function<Node<?>, Graph>() {
		@Override
		public Graph apply(final Node<?> input) {
			final Graph entity = new Graph();
			final Vertex root = toVertex.apply(input);
			entity.addVertex(root);
			processChildren(root, input, entity);
			return entity;
		}
		
		private void processChildren(final Vertex parent, final Node<?> node, final Graph graph) {
			for (final Node<?> child:node.getChildren()) {
				final Vertex vertex = toVertex.apply(child);
				graph.addVertex(vertex);
				new Edge(parent, vertex, graph);
				processChildren(vertex, child, graph);
			}
		}
	};

	
	private static final Function<Node<?>, Vertex> toVertex = new Function<Node<?>, Vertex>() {
		@Override
		public Vertex apply(final Node<?> input) {
			final Vertex entity = new Vertex();
			entity.setName(input.getName());
			entity.setValue(toValue.apply(input.getValue()));
			return entity;
		}
	};
	
	private static final Function<org.rosuda.type.Value, org.rosuda.graph.domain.Value> toValue = new Function<org.rosuda.type.Value, org.rosuda.graph.domain.Value>() {
		@Override
		public org.rosuda.graph.domain.Value apply(final org.rosuda.type.Value typeValue) {
			if (typeValue == null)
				return null;
			org.rosuda.graph.domain.Value domainValue = new org.rosuda.graph.domain.Value();
			switch (typeValue.getType()) {
				case BOOL: domainValue.setBool(typeValue.getBool()); break;
				case NUMBER: domainValue.setNumber(typeValue.getNumber()); break;
				case STRING: domainValue.setString(typeValue.getString()); break;
				case REFERENCE: domainValue.setReference(typeValue.getReference()); break;
			}
			return domainValue;
		}
	};

	public static final Function<Graph, Node<?>> convertGraphEntityToNode = new Function<Graph, Node<?>>() {
		@Override
		public Node<?> apply(final Graph graph) {
			if (graph == null)
				return null;
			final Map<Long, Vertex> vertexIdMap = Maps.newHashMap();
			final Map<Long, List<Long>> edgesMap = Maps.newHashMap();
			final Map<Long, Long> parentIdMap = Maps.newHashMap();
			
			//determine root node => no parent
			final Set<Long> ids = Sets.newHashSet();
			for (final Vertex vertex: graph.getVertices()) {
				vertexIdMap.put(vertex.getId(), vertex);
			}
			ids.addAll(vertexIdMap.keySet());
			for (final Edge edge: graph.getEdges()) {
				List<Long> edgesList = Lists.newArrayList();
				if (edgesMap.containsKey(edge.getFrom().getId())) {
					edgesList = edgesMap.get(edge.getFrom().getId());
				} else {
					edgesMap.put(edge.getFrom().getId(), edgesList);
				}
				edgesList.add(edge.getTo().getId());
				parentIdMap.put(edge.getTo().getId(), edge.getFrom().getId());
				ids.remove(edge.getTo().getId());
			}
			if (ids.size()!=1)
				throw new IllegalArgumentException("Tree has no root");
			//get RootId
			final GraphEntityHolder graphHolder = new GraphEntityHolder(graph, vertexIdMap, edgesMap, parentIdMap);
			final Long id = ids.iterator().next();
			return new GraphEntityFacade(graphHolder, id, new HashMap<Long, EntityConverter.GraphEntityFacade>());
		}		
	};
	
	private static final Function<org.rosuda.graph.domain.Value, org.rosuda.type.Value> fromEntityValue = new Function<org.rosuda.graph.domain.Value, org.rosuda.type.Value>() {
		@Override
		public Value apply(final org.rosuda.graph.domain.Value input) {
			if (input == null)
				return null;
			switch (input.getType()) {
				case BOOL: return org.rosuda.type.Value.newBool(input.getBool());
				case NUMBER: return org.rosuda.type.Value.newNumber(input.getNumber());
				case STRING: return org.rosuda.type.Value.newString(input.getString());
				case REFERENCE: return org.rosuda.type.Value.newReference(input.getReference());
				default: throw new UnsupportedOperationException("cannot convert unknown type:"+ input);
			}
		}
	};
		
	public interface Entity<T> {
		T getEntity();
	}
	
	private static class GraphEntityHolder implements Entity<Graph>{
		private final Graph graph;
		private final Map<Long, Vertex> vertexIdMap;
		private final Map<Long, List<Long>> edgesMap;
		private final Map<Long, Long> parentIdMap;
		
		private GraphEntityHolder(final Graph graph, final Map<Long, Vertex> vertexIdMap, final Map<Long, List<Long>> edgesMap, final Map<Long, Long> parentIdMap) {
			this.graph = graph; 
			this.vertexIdMap = Collections.unmodifiableMap(vertexIdMap);
			this.edgesMap = Collections.unmodifiableMap(edgesMap);
			this.parentIdMap = Collections.unmodifiableMap(parentIdMap);
		}
		
		@Override
		public Graph getEntity() {
			return graph;
		}
	}
	
	private static class GraphEntityFacade implements Node<Vertex>, Entity<Graph> {
		private final Long nodeId;
		private final GraphEntityHolder graph;
		private final Map<Long, GraphEntityFacade> cache;
		
		private GraphEntityFacade(final GraphEntityHolder graph, final Long nodeId, final Map<Long, GraphEntityFacade> cache) {
			this.graph = graph;
			this.nodeId = nodeId;
			this.cache = cache;
		}
		
		private Function<Long, GraphEntityFacade> toNodeFacade = new Function<Long, EntityConverter.GraphEntityFacade>() {
			@Override
			public GraphEntityFacade apply(final Long nodeId) {
				if (cache.containsKey(nodeId)) {
					return cache.get(nodeId);
				} else {
					final GraphEntityFacade facade = new GraphEntityFacade(graph, nodeId, cache);
					GraphEntityFacade.this.cache.put(nodeId, facade);
					return facade;
				}
			}
		};
	
		private Function<GraphEntityFacade, Node<Vertex>> facadeToNode = new Function<EntityConverter.GraphEntityFacade, Node<Vertex>>() {
			@Override
			public Node<Vertex> apply(final GraphEntityFacade facade) {
				return facade;
			}
		};
		
		private Function<Long, Node<Vertex>> toNode = Functions.compose(facadeToNode, toNodeFacade);
		
		@Override
		public Graph getEntity() {
			return this.graph.getEntity();
		}
		
		@Override
		public Node<Vertex> getParent() {
			final Long parentId = graph.parentIdMap.get(nodeId);
			if (parentId == null)
				return null;
			return toNodeFacade.apply(parentId);
		}

		@Override
		public Iterable<Node<Vertex>> getChildren() {
			final List<Long> children = graph.edgesMap.get(nodeId);
			if (children == null)
				return Collections.emptySet();
			return Lists.transform(children, toNode);
		}

		@Override
		public Node<Vertex> childAt(final int idx) {
			final List<Long> children = graph.edgesMap.get(nodeId);
			if (children == null)
				return null;
			return toNodeFacade.apply(children.get(idx));
		}

		@Override
		public int getChildCount() {
			final List<Long> children = graph.edgesMap.get(nodeId);
			if (children == null)
				return 0;
			return children.size();
		}

		@Override
		public Iterable<Node<Vertex>> getLinks() {
			throw new UnsupportedOperationException("this type does not support links");
		}

		@Override
		public Node<Vertex> linkAt(int idx) {
			throw new UnsupportedOperationException("this type does not support links");
		}

		@Override
		public int getLinkCount() {
			throw new UnsupportedOperationException("this type does not support links");
		}

		@Override
		public String getName() {
			return graph.vertexIdMap.get(nodeId).getName();
		}

		@Override
		public Value getValue() {
			return fromEntityValue.apply(graph.vertexIdMap.get(nodeId).getValue());
		}
		
	}
	
}
