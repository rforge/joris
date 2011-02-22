package org.rosuda.graph.domain;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import org.rosuda.type.Node;
import org.rosuda.type.Node.Builder;
import org.rosuda.type.NodeBuilderFactory;

import com.google.common.base.Function;

public class GraphWrapper implements NodeBuilderFactory<Vertex>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -930689831982539309L;

	private Graph delegate;
	
	private static class BuildInterceptionProxy implements InvocationHandler {

		private final Builder<Vertex> delegate;
		private final Graph graph;
		
		public BuildInterceptionProxy(Builder<Vertex> node, final Graph graph) {
			this.delegate = node;
			this.graph = graph;
		}

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args)
				throws Throwable {
			if ("build".equals(method.getName())) {
				final Node<Vertex> vertex = delegate.build();
				assert(vertex.getParent() == null);
				convertRecursive(vertex);
				return vertex;
			}
			//unwrap proxy
			if (Proxy.isProxyClass(proxy.getClass()) && (Proxy.getInvocationHandler(proxy) == this)) {
				//oh no ...
				return method.invoke(delegate, args);
			} else {
				return method.invoke(proxy, args);
			}
		}

		private Vertex convertRecursive(final Node<Vertex> nodeVertex) {
			final Vertex vertex = makeVertex.apply(nodeVertex);
			graph.addVertex(vertex);
			long order = 1;
			for (final Node<Vertex> child: nodeVertex.getChildren()) {
				final Edge edge = new Edge(vertex, convertRecursive(child), vertex.getGraph());
				edge.setSuccession(order ++);
//				graph.addEdge(edge);
			}
			return vertex;
		}
		
	}
	
	public static final Function<Node<Vertex>, Vertex> makeVertex = new Function<Node<Vertex>, Vertex>() {

		@Override
		public Vertex apply(final Node<Vertex> genericVertex) {
			final Vertex bean = new Vertex();
			bean.setName(genericVertex.getName());
			bean.setValue(convertValue.apply(genericVertex.getValue()));
			return bean;
		}
	};
	
	public static final Function<org.rosuda.type.Value, org.rosuda.graph.domain.Value> convertValue = new Function<org.rosuda.type.Value, org.rosuda.graph.domain.Value>() {

		@Override
		public org.rosuda.graph.domain.Value apply(final org.rosuda.type.Value value) {
			if (value == null)
				return null;
			final Value bean = new org.rosuda.graph.domain.Value();
			switch (value.getType()) {
				case BOOL: 		bean.setType(Value.Type.BOOL); bean.setBool(value.getBool()); break;
				case NUMBER: 	bean.setType(Value.Type.NUMBER); bean.setNumber(value.getNumber()); break;
				case STRING: 	bean.setType(Value.Type.STRING); bean.setString(value.getString()); break;
				case REFERENCE:	bean.setType(Value.Type.REFERENCE); bean.setString(value.getString()); break;
			}
			return bean;
		}
	};

	public Builder<Vertex> createRoot() {
		final Builder<Vertex> delegateBuilder = new org.rosuda.type.impl.Graph<Vertex>().createRoot();
		this.delegate = new Graph();
		return (Builder<Vertex>) Proxy.newProxyInstance(GraphWrapper.class.getClassLoader(), new Class[]{Node.Builder.class}, new BuildInterceptionProxy(delegateBuilder, this.delegate) );
	}
	
	public Collection<Vertex> getVertices() {
		return delegate.getVertices();
	}
	
	public Collection<Edge> getEdges() {
		return delegate.getEdges();
	}
	
	public Graph getGraph() {
		return delegate;
	}
}
