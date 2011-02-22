package org.rosuda.util.nodelistcalc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.type.Node;

public class ListCalculationTest {

	private List<Node<?>> data;

	private static Node<?> loadResource(final String resourceName) throws IOException, ClassNotFoundException {
		final ObjectInputStream ois = new ObjectInputStream(
				ListCalculationTest.class
						.getResourceAsStream(resourceName));
		final Object rootNode = ois.readObject();
		ois.close();
		return (Node<?>) rootNode;
	}

	@Before
	public void setUp() throws IOException, ClassNotFoundException {
		data = new ArrayList<Node<?>>();
		// load data
		for (int i=1; i<= 8 ;i++) {
			final String rscName = "/models/airquality-"+i+".rObj";
			data.add(loadResource(rscName));
		}
	}
	
	@Test
	public void testAICRatios() {
		Assert.assertNotNull(data);
		Assert.assertEquals(8, data.size());
		final ListCalculationUtil util = new ListCalculationUtil();
		util.setContent(data);
		//get all AICs:
		final List<Number> aics = util.calculate("AIC");
		Assert.assertEquals(8, aics.size());
		for (final Number aic : aics) {
			Assert.assertNotNull(aic);
		}
		//calculate AIC ratios:
		
	}
}
