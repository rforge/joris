package org.rosuda.graph.service.search;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.graph.service.GraphService;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;

import com.google.common.collect.Lists;

/**
 * this test needs be fulfilled by all APIs, just replace service using spring
 * 
 * @author ralfseger
 * 
 */
public class ConstrainedSearchBaseTest implements InvocationHandler {

    @SuppressWarnings("rawtypes")
    private GraphService service;
    @SuppressWarnings("rawtypes")
    protected Node node;
    @SuppressWarnings("rawtypes")
    protected List<Node> expectedObject;

    private List<Invocation> invocationList = Lists.newArrayList();

    public static class Invocation {

	private final Method method;
	private final Object[] args;

	public Invocation(Method method, Object[] args) {
	    this.method = method;
	    this.args = args;
	}

	public Method getMethod() {
	    return method;
	}

	public Object[] getArgs() {
	    return args;
	}

    }

    @Before
    public void setUp() {
	final NodeBuilderFactory<Object> nodeBuilderFactory = new NodeBuilderFactory<Object>();
	// TODO; create reference mock objects for storage and retrieval!
	final Node.Builder<Object> root = nodeBuilderFactory.createRoot();
	root.add(root.createChild("model"));
	service = (GraphService<?>) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { GraphService.class }, this);
    }

    protected void setGraphService(final GraphService<?> service) {
	this.service = service;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchExpectedNode() {
	this.expectedObject = Collections.singletonList(node);
	final List<Node<?>> returnedObj = service.find(null);
	assertEquals(1, returnedObj.size());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	invocationList.add(new Invocation(method, args));
	return expectedObject;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindAModel() {
	service.store(null);
	VertexConstraint distConstraint = new NameVertexConstraint("dist").addChildConstraint(
		new NameVertexConstraint("estimate").addValueConstraint(new NumberValueConstraint(0, Relation.GT)).addValueConstraint(
			new NumberValueConstraint(0.15, Relation.LT))).addChildConstraint(
		new NameVertexConstraint("p-value").addValueConstraint(new NumberValueConstraint(0, Relation.GT)).addValueConstraint(
			new NumberValueConstraint(1e-10, Relation.LT)));
	// TODO store a graph and test search results
    }
}
