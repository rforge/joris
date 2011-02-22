package org.rosuda.mapper.filter;


import junit.framework.TestCase;

import org.rosuda.mapper.filter.NodeFilter.Event;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;

public class NodeFilterTest extends TestCase {

	private static final boolean debug = true;
	
	int countRejected = 0;
	int countAccepted = 0;
	
	ObjectTransformationManager<Object> mgr;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mgr = new ObjectTransformationManager<Object> (new NodeBuilderFactory<Object>());
	}
	
	public void testNoFilter() {
		Node<Object> rootNode = mgr.transform(1);
		assertNotNull(rootNode);
		assertEquals(1,rootNode.getChildCount());
		assertNotNull(rootNode.childAt(0).getValue());
		if (debug) System.out.println(rootNode);
	}
	
	public void testFilter() {
		final NodeFilter.EventListener<Object> listener = new NodeFilter.EventListener<Object>() {
			public void triggered(Event<Object> event) {
				if (event.wasAccepted())
					countAccepted ++;
				else 
					countRejected ++;
			}
		};
		final NodeFilter<Object> filterall = new NodeFilter<Object>() {
			@Override
			protected boolean mayCreateChild(final Node.Builder<Object> parent, final String... newNodeName) {
				return false;
			}
			
		};
		filterall.addListener(listener);
		mgr.addFilter(filterall);
		Node<Object> rootNode = mgr.transform(1);
		if (debug) System.out.println(rootNode);
		assertNotNull(rootNode);
		assertEquals(1, countRejected);
		assertEquals(0, countAccepted);
		assertEquals(0,rootNode.getChildCount());
	}
}
