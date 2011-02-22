package org.rosuda.mapper.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.type.Node;

public class ObjectTransformationHandlerTest extends TestCase {

	private static final boolean debug = false;
	
	private ObjectTransformationHandler<Object> transformationHandler;
	private org.rosuda.type.NodeBuilderFactory<Object> nodeBuilderFactory;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		transformationHandler = new ObjectTransformationHandler<Object>();
		nodeBuilderFactory = new org.rosuda.type.impl.NodeBuilderFactory<Object>();
	}
	
	public ObjectTransformationHandlerTest() {
		super("HanderTest");
	}

	public void testCreateNull() throws ParserConfigurationException {
		final Node.Builder<Object> rootNode = nodeBuilderFactory.createRoot();
		assertNotNull(rootNode);
		transformationHandler.transform(null, rootNode);
		assertNotNull(rootNode);
		assertEquals(0, rootNode.getChildCount());
		assertNull(rootNode.getValue());
		if (debug) System.out.println(rootNode);
	}

	public void testCreateString() {
		final Node.Builder<Object> rootNode = nodeBuilderFactory.createRoot();
		transformationHandler.transform("string-value", rootNode);
		assertNotNull(rootNode);
		assertTrue(rootNode.getChildren().iterator().hasNext());
		assertNull(rootNode.getValue());
		assertTrue(rootNode.getChildCount()>0);
		assertNull(rootNode.getValue());
		assertEquals(1, rootNode.getChildCount());
		Iterator<Node.Builder<Object>> nodes = rootNode.getChildren().iterator();
		final Node.Builder<Object> child = nodes.next();
		assertEquals(String.class.getSimpleName(), child.getName());
		assertEquals("string-value", child.getValue().getString());
		if (debug) System.out.println(rootNode);
	}
	
	public void testCreateInteger() {
		final Node.Builder<Object> rootNode = nodeBuilderFactory.createRoot();
		assertNotNull(rootNode);
		transformationHandler.transform(1, rootNode);
		assertNotNull(rootNode);
		assertTrue(rootNode.getChildCount()>0);
		assertNull(rootNode.getValue());
		assertEquals(1, rootNode.getChildCount());
		Iterator<Node.Builder<Object>> nodes = rootNode.getChildren().iterator();
		final Node.Builder<Object> child = nodes.next();
		assertEquals(Integer.class.getSimpleName(), child.getName());
		assertEquals(1, child.getValue().getNumber());
		if (debug) System.out.println(rootNode);
	}

	public void testCreateDouble() {
		final Node.Builder<Object> rootNode = nodeBuilderFactory.createRoot();
		assertNotNull(rootNode);
		transformationHandler.transform(1.42, rootNode);//TODO test 1.0 (double) staying double!
		assertNotNull(rootNode);
		assertTrue(rootNode.getChildCount()>0);
		assertNull(rootNode.getValue());
		assertEquals(1, rootNode.getChildCount());
		Iterator<Node.Builder<Object>> nodes = rootNode.getChildren().iterator();
		final Node.Builder<Object> child = nodes.next();
		assertEquals(Double.class.getSimpleName(), child.getName());
		assertEquals(1.42, child.getValue().getNumber());
		if (debug) System.out.println(rootNode);
	}
	
	public void testCreateBoolean() {
		final Node.Builder<Object> rootNode = nodeBuilderFactory.createRoot();
		assertNotNull(rootNode);
		transformationHandler.transform(false, rootNode);
		assertNotNull(rootNode);
		assertTrue(rootNode.getChildCount()>0);
		assertNull(rootNode.getValue());
		assertEquals(1, rootNode.getChildCount());
		Iterator<Node.Builder<Object>> nodes = rootNode.getChildren().iterator();
		final Node.Builder<Object> child = nodes.next();
		assertEquals(Boolean.class.getSimpleName(), child.getName());
		assertEquals(false, child.getValue().getBool());
		if (debug) System.out.println(rootNode);
	}

	public void testComplexObject() {
		final Node.Builder<Object> complexNode = nodeBuilderFactory.createRoot();
		// test with this
		//final TransformationBean<String> bean = new TransformationBean<String>("test");
		transformationHandler.transform(this, complexNode);
		assertNotNull(complexNode);
		assertEquals(1, complexNode.getChildCount());
		if (debug) System.out.println(complexNode);
	}
	
	public void testArray() {
		final Node.Builder<Object> arrayNode = nodeBuilderFactory.createRoot();
		ObjectTransformationHandlerTest[] array = new ObjectTransformationHandlerTest[] { this, this, this };
		transformationHandler.transform(array, arrayNode);
		//System.out.println(verboseXML(arrayNode));
		assertNotNull(arrayNode);
		//list is internal first node (wrapper)
		assertEquals(1, arrayNode.getChildCount());
		final Node.Builder<Object> firstChild = arrayNode.getChildren().iterator().next();
		assertEquals(3, firstChild.getChildCount());
		if (debug) System.out.println(arrayNode);
	}
	
	public void testList() {
		// what about a list
		final List<ObjectTransformationHandlerTest> list = new ArrayList<ObjectTransformationHandlerTest>();
		list.add(this);
		list.add(this);
		final Node.Builder<Object> listNode = nodeBuilderFactory.createRoot();
		transformationHandler.transform(list, listNode);
		//System.out.println(verboseXML(listNode));
		assertNotNull(listNode);
		assertEquals(1, listNode.getChildCount());
		final Node.Builder<Object> firstChild = listNode.getChildren().iterator().next();
		assertEquals(2, firstChild.getChildCount());
		if (debug) System.out.println(listNode);
	}
	
	public void testMap() {
		// a map ?
		final Map<String, ObjectTransformationHandlerTest> map = new HashMap<String, ObjectTransformationHandlerTest>();
		map.put("x1\"\"<>", this);
		map.put("x2", this);
		final Node.Builder<Object> mapNode = nodeBuilderFactory.createRoot();
		transformationHandler.transform(map, mapNode);
		//System.out.println(verboseXML(mapNode));
		assertNotNull(mapNode);
		assertEquals(1, mapNode.getChildCount());
		final Node.Builder<Object> firstChild = mapNode.getChildren().iterator().next();
		
		assertEquals(2, firstChild.getChildCount());
		if (debug) System.out.println(mapNode);
	}

	public void testLoop() {
		final List<List<?>> loop = new ArrayList<List<?>>();
		loop.add(loop);
		
		final Node.Builder<Object> loopNode = nodeBuilderFactory.createRoot();
		transformationHandler.transform(loop, loopNode);
		//System.out.println(verboseXML(mapNode));
		assertNotNull(loopNode);
		assertEquals(1, loopNode.getChildCount());
		final Node.Builder<Object> firstChild = loopNode.getChildren().iterator().next();
		
		assertEquals(1, firstChild.getChildCount());
		if (debug) System.out.println(loopNode);
	}
}
