package org.rosuda.type.impl;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rosuda.type.Node;
import org.rosuda.type.Node.Builder;
import org.rosuda.type.NodeBuilderFactory;
import org.rosuda.type.Value;

public class Graph<T> implements NodeBuilderFactory<T>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9152805012165333039L;
	//	static int factoryIdx = 0;
	int nodeIdx = 0;
	
	interface NodeValue {
		String getName();
		Value getValue();
	}
	
	protected class NodeValueBuilderImpl implements NodeValue {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7582259642347834847L;
		private String name;
		private Value value;
		
		public String getName() {
			return name;
		}
		public Value getValue() {
			return value;
		}
	}
	
	protected class NodeValueImpl implements NodeValue, Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1541600745920227222L;
		private final String name;
		private final Value value;
		public NodeValueImpl(final String name, final Value value) {
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public Value getValue() {
			return value;
		}
	}

	protected Map<Integer, NodeValue> nodes = new TreeMap<Integer, NodeValue>();
	protected Map<Integer, Integer> parentRefs = new HashMap<Integer, Integer>();
	protected Map<Integer, List<Integer>> childRefs = new HashMap<Integer, List<Integer>>();
	protected Map<Integer, List<Integer>> linkRefs = new HashMap<Integer, List<Integer>>();

	
	public Node.Builder<T> createRoot() {
//		factoryIdx ++;
		final NodeValueBuilderImpl rootValue = new NodeValueBuilderImpl();
		rootValue.name = Node.ROOTNAME;
		nodes.put(nodeIdx, rootValue);
		return new BuilderFacade(nodeIdx);
	}
		
	protected class BuilderFacade implements Node.Builder<T> {
		
		final int nodeIdx;
		
		protected BuilderFacade(final int nodeIdx) {
			this.nodeIdx = nodeIdx;
		}

		public Builder<T> getParent() {
			if (!parentRefs.containsKey(nodeIdx))
				return null;
			return new BuilderFacade(parentRefs.get(nodeIdx));
		}

		public Iterable<Builder<T>> getChildren() {
			final List<Integer> childIdxList = childRefs.get(nodeIdx);
			if (childIdxList == null)
				return Collections.emptyList();	
			return new Iterable<Node.Builder<T>>() {
				public Iterator<Builder<T>> iterator() {
					return new Iterator<Node.Builder<T>>() {
						int i=0;
						
						public boolean hasNext() {
							return (i < childIdxList.size());
						}
						public Builder<T> next() {
							return new BuilderFacade(childIdxList.get(i++));
						}
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
		}

		public Builder<T> childAt(final int idx) {
			return new BuilderFacade(childRefs.get(nodeIdx).get(idx));
		}

		public int getChildCount() {
			if (!childRefs.containsKey(nodeIdx))
				return 0;
			return childRefs.get(nodeIdx).size();
		}

		public Iterable<Builder<T>> getLinks() {
			final List<Integer> linkIdxList = linkRefs.get(nodeIdx);
			if (linkIdxList == null)
				return Collections.emptyList();	
			return new Iterable<Node.Builder<T>>() {
				public Iterator<Builder<T>> iterator() {
					return new Iterator<Node.Builder<T>>() {
						int i=0;
						
						public boolean hasNext() {

							return (i < linkIdxList.size());
						}
						public Builder<T> next() {
							return new BuilderFacade(linkIdxList.get(i++));
						}
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
		}

		public Builder<T> linkAt(final int idx) {
			return new BuilderFacade(linkRefs.get(nodeIdx).get(idx));
		}

		public int getLinkCount() {
			if (!linkRefs.containsKey(nodeIdx))
				return 0;
			return linkRefs.get(nodeIdx).size();
		}

		public String getName() {
			if (!nodes.containsKey(nodeIdx))
				return null;
			return nodes.get(nodeIdx).getName();
		}

		public Value getValue() {
			if (!nodes.containsKey(nodeIdx))
				return null;
			return nodes.get(nodeIdx).getValue();
		}

		public void add(final Builder<T> child) {
			if (child instanceof Graph.BuilderFacade) {
				final BuilderFacade childFacade = (BuilderFacade) child;
				if (parentRefs.containsKey(childFacade.nodeIdx)) {
					throw new IllegalStateException("child already added under other parent");
				} else {
					parentRefs.put(childFacade.nodeIdx, nodeIdx);
				}
				final List<Integer> children;
				if (childRefs.containsKey(nodeIdx)) {
					children = childRefs.get(nodeIdx);
				} else {
					children = new ArrayList<Integer>();
					childRefs.put(nodeIdx, children);
				}
				children.add(childFacade.nodeIdx);
			}
			 
		}
		
		//experimental
		@SuppressWarnings("unused")
		private BuilderFacade cast(final Builder<T> builder) {
			if (builder instanceof Graph.BuilderFacade) {
				return (BuilderFacade) builder;
			} else {
				final TypeVariable<?>[] thisTypes = Graph.this.getClass().getTypeParameters();
				final TypeVariable<?>[] builderTypes = builder.getClass().getTypeParameters();
				if (thisTypes.length == builderTypes.length) {
					int matches = 0;
					for (int i=0;i<thisTypes.length;i++) {
						if (builderTypes[i].getClass() == thisTypes[i].getClass()) {
							matches ++;	
						}
					}
					if (matches == thisTypes.length) {
						System.out.println("internally matched!");
						//cast ?
						final BuilderFacade graphNode = (BuilderFacade) createChild(builder.getName());
						graphNode.setValue(builder.getValue());
						if (builder.getChildCount() > 0) {
							System.out.println("children found ..");
						}
						if (builder.getParent() != null) {
							System.out.println("parent found ..");
						}
						return graphNode;
					}
				}
				throw new UnsupportedOperationException("wrong argument type : "+builder.getClass()+ ", expected:"+BuilderFacade.class);
			}
			
		}

		public Builder<T> createChild(final String... nodeNames) {
			final NodeValueBuilderImpl newValue = new NodeValueBuilderImpl();
			if (nodeNames != null && nodeNames.length == 1) {
				newValue.name = nodeNames[0];
			}
			final int nodeIdx = ++Graph.this.nodeIdx;
			nodes.put(nodeIdx, newValue);
			return new BuilderFacade(nodeIdx);
		}

		public void createReference(Builder<T> anotherNode) {
			if (anotherNode instanceof Graph.BuilderFacade) {
				final List<Integer> links;
				if (linkRefs.containsKey(nodeIdx)) {
					links = linkRefs.get(nodeIdx);
				} else {
					links = new ArrayList<Integer>();
					linkRefs.put(nodeIdx, links);
				}
				final BuilderFacade facade = (BuilderFacade) anotherNode;
				links.add(facade.nodeIdx);
			} else throw new UnsupportedOperationException("wrong argument type : "+anotherNode.getClass()+ ", expected:"+BuilderFacade.class);
			
		}

		public void delete(Builder<T> child) {
			if (child instanceof Graph.BuilderFacade) {
				final BuilderFacade childFacade = (BuilderFacade) child;
				//unbind
				childFacade.removeFromParent();
				nodes.remove(childFacade.nodeIdx);
			}
			else throw new UnsupportedOperationException("wrong argument type : "+child.getClass()+ ", expected:"+BuilderFacade.class);

		}
		
		public Builder<T> removeFromParent() {
			final int parentIdx = parentRefs.get(this.nodeIdx);
			parentRefs.remove((Integer)this.nodeIdx);
			final List<Integer> childIndizes = childRefs.get(parentIdx);
			childIndizes.remove((Integer)this.nodeIdx);
			return this;
		}
		

		@SuppressWarnings("unchecked")
		public void setValue(final Value value) {
			final NodeValueBuilderImpl nodeValue = (NodeValueBuilderImpl) nodes.get(nodeIdx);
			nodeValue.value = value;
		}

		public Node<T> build() {
			//1. freeze structure (immodifiable)
			for (final Map.Entry<Integer, NodeValue> entry : nodes.entrySet()) {
				final NodeValue nodeValue = entry.getValue();
				nodes.put(entry.getKey(), new NodeValueImpl(nodeValue.getName(), nodeValue.getValue()));
			}
			nodes = Collections.unmodifiableMap(nodes);
			parentRefs = Collections.unmodifiableMap(parentRefs);
			//wrap lists too
			for (final Map.Entry<Integer, List<Integer>> entry : childRefs.entrySet()) {
				childRefs.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
			}
			childRefs = Collections.unmodifiableMap(childRefs);
			return new NodeFacade(nodeIdx);
		}
		
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("Node.Builder(internalNodeIdx=")
			.append(nodeIdx);
			if (nodes.containsKey(nodeIdx)) {
				builder.append(", name=")
						.append(nodes.get(nodeIdx).getName());
			}
			builder.append(")");
			return builder.toString();
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof Graph.BuilderFacade) {
				final Graph.BuilderFacade facade = (Graph.BuilderFacade) obj;
				return facade.nodeIdx == this.nodeIdx;
			}
			return super.equals(obj);
		}
	}

	protected class NodeFacade implements Node<T>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6391980757768457119L;
		final int nodeIdx;
		
		protected NodeFacade(final int nodeIdx) {
			this.nodeIdx = nodeIdx;
		}

		public Node<T> getParent() {
			if (!parentRefs.containsKey(nodeIdx))
				return null;
			return new NodeFacade(parentRefs.get(nodeIdx));
		}

		public Iterable<Node<T>> getChildren() {
			final List<Integer> childIdxList = childRefs.get(nodeIdx);
			if (childIdxList == null)
				return Collections.emptyList();	

			return new Iterable<Node<T>>() {
				public Iterator<Node<T>> iterator() {
					return new Iterator<Node<T>>() {
						int i=0;
						public boolean hasNext() {
							/* if (childIdxList == null)
								return false; */
							return (i < childIdxList.size());
						}
						public Node<T> next() {
							return new NodeFacade(childIdxList.get(i++));
						}
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
		}

		public Node<T> childAt(int idx) {
			return new NodeFacade(childRefs.get(nodeIdx).get(idx));
		}

		public int getChildCount() {
			if (!childRefs.containsKey(nodeIdx))
				return 0;
			return childRefs.get(nodeIdx).size();
		}
		
		public Iterable<Node<T>> getLinks() {
			final List<Integer> linkIdxList = linkRefs.get(nodeIdx);
			if (linkIdxList == null)
				return Collections.emptyList();	
			return new Iterable<Node<T>>() {
				public Iterator<Node<T>> iterator() {
					return new Iterator<Node<T>>() {
						int i=0;
						
						public boolean hasNext() {
							return (i < linkIdxList.size());
						}
						public Node<T> next() {
							return new NodeFacade(linkIdxList.get(i++));
						}
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
		}

		public Node<T> linkAt(final int idx) {
			return new NodeFacade(linkRefs.get(nodeIdx).get(idx));
		}

		public int getLinkCount() {
			if (!linkRefs.containsKey(nodeIdx))
				return 0;
			return linkRefs.get(nodeIdx).size();
		}



		public String getName() {
			return nodes.get(nodeIdx).getName();
		}

		public Value getValue() {
			return nodes.get(nodeIdx).getValue();
		}
		
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("Node(name=")
			.append(nodes.get(nodeIdx).getName())
			.append(")")
			;
			return builder.toString();
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof Graph.NodeFacade) {
				final Graph.NodeFacade facade = (Graph.NodeFacade) obj;
				return facade.nodeIdx == this.nodeIdx;
			}
			return false;
		}
	}
	
	
}
