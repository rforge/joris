package org.rosuda.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.rosuda.graph.domain.GraphWrapper;
import org.rosuda.graph.domain.Value;
import org.rosuda.graph.domain.Vertex;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.type.Node;

@RunWith(BlockJUnit4ClassRunner.class)
public class ConversionTest{

	private ObjectTransformationHandler<Vertex> transformationHandler;
	private GraphWrapper graph;
	private Node.Builder<Vertex> target;
	
	@Before
	public void setUp() throws Exception {
		transformationHandler = new ObjectTransformationHandler<Vertex>();
		graph = new GraphWrapper();
		target = graph.createRoot();
		final List<List<String>> source = new ArrayList<List<String>>();
		final List<String> list1 = new ArrayList<String>();
		final List<String> list2 = new ArrayList<String>();
		source.add(list1);
		source.add(list2);
		list1.add("erstes Element");
		list2.add("erstes Zweit-Element");
		list2.add("zweites Zweit-Element");
		transformationHandler.transform(source, target);
	}
	
	@Test
	public void testConvertGenericNodesToPojo() {
		Assert.assertNotNull(graph);
		//assert Graph is empty before build is called
		Assert.assertTrue(graph.getVertices().isEmpty());
//		Assert.assertTrue(graph.getEdges().isEmpty());
		//call Build
		target.build();
		Assert.assertFalse(graph.getVertices().isEmpty());
//		Assert.assertFalse(graph.getEdges().isEmpty());

		final Map<String, Integer> countedTypes = new HashMap<String, Integer>();
		final Map<Value.Type, Integer> countedValueTypes = new HashMap<Value.Type, Integer>();
		
		for (final Vertex v: graph.getVertices()) {
			final String vName = v.getName();
			if (countedTypes.containsKey(vName)) {
				countedTypes.put(vName, 1 + countedTypes.get(vName));
			} else {
				countedTypes.put(vName, 1);
			}
			if (v.getValue() == null)
				continue;
			final Value.Type vType = v.getValue().getType();
			if (vType!=null) {
				if (countedValueTypes.containsKey(vType)) {
					countedValueTypes.put(vType, 1 + countedValueTypes.get(vType));
				} else {
					countedValueTypes.put(vType, 1);
				}				
			}
		}
		Assert.assertEquals(1 , (int) countedTypes.get("root"));
		Assert.assertEquals(2 , (int) countedTypes.get("ArrayList"));
		Assert.assertEquals(3 , (int) countedTypes.get("iterable"));
		Assert.assertEquals(6 , (int) countedTypes.get("String"));
		
		Assert.assertEquals(1, countedValueTypes.size());
		Assert.assertEquals(3, (int) countedValueTypes.get(Value.Type.STRING));
				
		Assert.assertEquals(12, graph.getVertices().size());
//		Assert.assertEquals(11, graph.getEdges().size());
	}	
}
