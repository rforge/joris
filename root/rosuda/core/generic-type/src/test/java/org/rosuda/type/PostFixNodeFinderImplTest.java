package org.rosuda.type;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.type.Node.Builder;

public class PostFixNodeFinderImplTest {

    private PostfixNodeFinderImpl<Number> finder;
    private Node<Number> data;
    
    @Before
    public void setUp() {
	finder = new PostfixNodeFinderImpl<Number>();
	final NodeBuilderFactory<Number> graphBuilder = new org.rosuda.type.impl.NodeBuilderFactory<Number>();
	Builder<Number> rootBuilder = graphBuilder.createRoot();
	Builder<Number> child1 = addNamedChildToParent(rootBuilder, "child1");
	Builder<Number> child2 = addNamedChildToParent(rootBuilder, "child2");
	Builder<Number> child1_1 = addNamedChildToParent(child1, "child1_1");
	addNamedChildToParent(child2, "child2_1");
	Builder<Number> child2_2 = addNamedChildToParent(child2, "child2_2");
	Builder<Number> child1_1_1 = addNamedChildToParent(child1_1, "child1_1_1");
	Builder<Number> child2_2_1 = addNamedChildToParent(child2, "child2_2_1");
	Builder<Number> child2_2_2 = addNamedChildToParent(child2_2, "child2_2_2");
	child1_1_1.setValue(Value.newNumber(3));
	child2_2_1.setValue(Value.newNumber(5));
	child2_2_2.setValue(Value.newNumber(6));
	data = rootBuilder.build();
    }


    @Test
    public void postFixFindsFullPathInclusiveRoot() {
	assertThat(finder.findNode(data, Node.ROOTNAME+"/child1/child1_1/child1_1_1"), notNullValue());
    }
    
    @Test
    public void postFixFindsFullMinPath() {
	assertThat(finder.findNode(data, "//child2/child2_2/child2_2_2"), notNullValue());
    }
    
    @Test
    public void postFixFindsFullMaxPath() {
	assertThat(finder.findNode(data, "//child1/child1_1/child1_1_1"), notNullValue());
    }

    @Test
    public void postFixFindsExpectedElements() {
	assertThat(finder.findNode(data, "//child1_1_1"), notNullValue());
    }

    @Test
    public void postFixFindsExpectedValue() {
	assertThat(finder.findNode(data, "//child1_1_1").getValue(), notNullValue());
	assertThat(finder.findNode(data, "//child2_2_1").getValue().getNumber().intValue(), equalTo(5));
	assertThat(finder.findNode(data, "//child2_2_2").getValue().getNumber().intValue(), equalTo(6));
    }
    
    // -- helper
    private Builder<Number> addNamedChildToParent(Builder<Number> parent, String childName) {
	Builder<Number> child1 = parent.createChild(childName);
	parent.add(child1);
	return child1;
    }
}
