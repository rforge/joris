package org.rosuda.type;



public interface Node<N>{

	public static final String ROOTNAME = "root";

	/**
	 * parent node in tree structure
	 * @return
	 */
	Node<N> getParent();

	/**
	 * guaranteed loop-free
	 * @return
	 */
	Iterable<Node<N>> getChildren();

	/**
	 * get the idx-th child
	 * @param idx
	 * @return
	 */
	Node<N> childAt(final int idx);

	/**
	 * the child count
	 * @return
	 */
	int getChildCount();

	/**
	 * linked nodes - not necessarily loop-free
	 * @return
	 */
	Iterable<Node<N>> getLinks();

	/**
	 * get the idx-th link
	 * @param idx
	 * @return
	 */
	Node<N> linkAt(final int idx);

	/**
	 * the link cound
	 * @return
	 */
	int getLinkCount();

	/**
	 * a name of the current Node
	 * @return
	 */
	String getName();

	/**
	 * a value of the current Node
	 * @return
	 */
	Value getValue();

	/**
	 * the builder is still mutable, and can be restructured
	 * whenever the BUILD() method is called an immutable node
	 * is returned and the builder is required to throw exceptions
	 * whenever Builder values/structure is changed
	 * 
	 * @param nodeName
	 * @return
	 */
	public interface Builder<B> {

		Builder<B> getParent();

		Iterable<Builder<B>> getChildren();

		Builder<B> childAt(final int idx);

		int getChildCount();

		Iterable<Builder<B>> getLinks();

		Builder<B> linkAt(final int idx);

		int getLinkCount();

		String getName();

		Value getValue();
		
		void add(final Builder<B> child);
		
		Builder<B> createChild(final String... nodeNames);
		
		void createReference(final Builder<B> anotherNode);

		/**
		 * removes the child (and the value) from the graph
		 * @param child
		 */
		void delete(final Builder<B> child);

		/**
		 * undo "add"
		 * @return
		 */
		Builder<B> removeFromParent();
		
		void setValue(final Value value);
		
		Node<B> build();
		
		//Class<? extends B> getType();
	}
	
}
