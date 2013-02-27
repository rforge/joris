package org.rosuda.type;

import org.rosuda.type.impl.Graph;

import junit.framework.TestCase;

public class NodeReferenceTest extends TestCase {

    // normally the BuilderFactory should be parametrized with a special node
    // type
    // for test purposes the complete genereic NodeBuilderFactory features any
    private NodeBuilderFactory<Graph<Object>> factory;

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	this.factory = new org.rosuda.type.impl.NodeBuilderFactory<Graph<Object>>();
    }

    public void testCreateBuilder() {
	assertNotNull(factory.createRoot());
    }

    public void testCreateBuildRoot() {
	final Node.Builder<Graph<Object>> rootBuilder = factory.createRoot();
	final Node<Graph<Object>> rootNode = rootBuilder.build();
	assertNotNull(rootNode);
	assertNotSame(rootBuilder, rootNode);
	assertNull(rootNode.getParent());
	assertEquals(0, rootNode.getChildCount());
    }

    public void testSimpleLoop() {
	final Node.Builder<Graph<Object>> rootBuilder = factory.createRoot();
	rootBuilder.add(rootBuilder);
	final Node<Graph<Object>> impl = rootBuilder.build();
	assertNotNull(impl);
	assertNotNull(impl.getParent());
	assertEquals(1, impl.getChildCount());
    }

    public void testCreateLoopByReference() {
	final Node.Builder<Graph<Object>> rootBuilder = factory.createRoot();
	rootBuilder.createReference(rootBuilder);
	Exception exc = null;
	try {
	    final Node<Graph<Object>> rootNode = rootBuilder.build();
	    assertNotNull(rootNode);

	} catch (final IllegalArgumentException x) {
	    exc = x;
	}
	assertNull(exc);
    }

    public void testStructure() {
	final Node.Builder<Graph<Object>> rootBuilder = factory.createRoot();
	Node.Builder<Graph<Object>> child = rootBuilder.createChild("child");
	rootBuilder.add(child);
	child = rootBuilder.createChild("child");
	rootBuilder.add(child);
	assertEquals(2, rootBuilder.getChildCount());
	// level 2
	final Node.Builder<Graph<Object>> parent = child;
	for (int i = 0; i < 3; i++) {
	    child = rootBuilder.createChild("child");
	    parent.add(child);
	}
	assertEquals(2, rootBuilder.getChildCount());
	assertEquals(3, parent.getChildCount());

	child.createReference(rootBuilder);

	parent.delete(child);
	rootBuilder.add(child);

	assertEquals(3, rootBuilder.getChildCount());
	final Node<Graph<Object>> root = rootBuilder.build();
	assertEquals(3, root.getChildCount());

	assertEquals(2, parent.getChildCount());
    }

    public void testStructuralBackRef() {
	final Node.Builder<Graph<Object>> rootBuilder = factory.createRoot();
	Node.Builder<Graph<Object>> child = rootBuilder.createChild("child");
	rootBuilder.add(child);
	child = rootBuilder.createChild("child");
	rootBuilder.add(child);
	assertEquals(2, rootBuilder.getChildCount());
	// level 2
	final Node.Builder<Graph<Object>> parent = child;
	for (int i = 0; i < 3; i++) {
	    child = rootBuilder.createChild("child");
	    parent.add(child);
	}
	assertEquals(2, rootBuilder.getChildCount());
	assertEquals(3, parent.getChildCount());

	// this child has been added - should be root/child[x]
	rootBuilder.createReference(child);
	assertEquals(2, rootBuilder.getChildCount());
	assertEquals(1, rootBuilder.getLinkCount());

	parent.delete(child);
	assertEquals(2, parent.getChildCount());
	rootBuilder.add(child);

	// reference IS a special link
	assertEquals(3, rootBuilder.getChildCount());
	assertEquals(1, rootBuilder.getLinkCount());

	final Node<Graph<Object>> rootNode = rootBuilder.build();
	assertEquals(3, rootNode.getChildCount());
	assertEquals(1, rootNode.getLinkCount());

	assertEquals(2, parent.getChildCount());
    }
}
