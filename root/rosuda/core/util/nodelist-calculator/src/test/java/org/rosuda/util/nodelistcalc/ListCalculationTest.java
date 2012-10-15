package org.rosuda.util.nodelistcalc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.type.PostfixNodeFinderImpl;

public class ListCalculationTest {

    private List<Node<IREXP>> data;
    private static final double EPSILON = 0.000001;

    @SuppressWarnings("unchecked")
    private static Node<IREXP> loadResource(final String resourceName) throws IOException, ClassNotFoundException {
	final ObjectInputStream ois = new ObjectInputStream(ListCalculationTest.class.getResourceAsStream(resourceName));
	final Object rootNode = ois.readObject();
	ois.close();
	return (Node<IREXP>) rootNode;
    }

    @Before
    public void setUp() throws IOException, ClassNotFoundException {
	data = new ArrayList<Node<IREXP>>();
	// load data
	for (int i = 1; i <= 8; i++) {
	    final String rscName = "/models/airquality-" + i + ".rObj";
	    data.add(loadResource(rscName));
	}
    }

    @Test
    public void testAICRatios() {
	Assert.assertNotNull(data);
	Assert.assertEquals(8, data.size());
	final ListCalculationUtil<IREXP> util = new ListCalculationUtil<IREXP>();
	util.setContent(data);
	// get all AICs:
	final List<Number> aics = util.calculate("AIC");
	Assert.assertEquals(8, aics.size());
	for (final Number aic : aics) {
	    Assert.assertNotNull(aic);
	}
	// calculate AIC ratios:
	// TODO test case for parser (seems to hang)
	// final List<Number> aicDiffs = util.calculate("cmin(${AIC}-$AIC");

	final List<Number> aicDiffs = util.calculateAs("${AIC}-cmin(${AIC}", "AICdiff");
	for (final Number aicDiff : aicDiffs) {
	    Assert.assertNotNull(aicDiff);
	}
	final List<Number> atts = util.calculate("@AICdiff");
	for (final Number att : atts) {
	    Assert.assertNotNull(att);
	}

	// TODO das geht noch net!
	final List<Number> aicLikeliHood = util.calculate("exp(-0.5*${@AICdiff})");
	for (final Number alh : aicLikeliHood) {
	    Assert.assertNotNull(alh);
	}

	final List<Number> aicRatios = util.calculate("exp(-0.5*${@AICdiff})/csum(exp(-0.5*${@AICdiff}))");
	for (final Number awt : aicRatios) {
	    Assert.assertNotNull(awt);
	}

	// given test data
	// =============================================
	// # AIC AICdiff AICwt AICratio
	// ---------------------------------------------
	// 0 1067,71 68,99 1,05E-015 5,23E-016
	// 1 1093,19 94,47 3,06E-021 1,53E-021
	// 2 1049,74 51,02 8,32E-012 4,16E-012
	// 3 1083,71 85 3,49E-019 1,75E-019
	// 4 1020,82 22,1 1,59E-005 7,93E-006
	// 5 1033,82 35,1 2,39E-008 1,20E-008
	// 6 998,72 0 1,00E+000 5,00E-001
	// 7 998,72 0 1,00E+000 5,00E-001

	Assert.assertEquals(1.59e-005, aicLikeliHood.get(4).doubleValue(), EPSILON);
	Assert.assertEquals(7.93e-006, aicRatios.get(4).doubleValue(), EPSILON);

    }
    
    @Test 
    public void pathCanAutocompleteRootPrefixButPrefixIsNotRequired() {
	final ListCalculationUtil<IREXP> util = new ListCalculationUtil<IREXP>();
	util.setContent(data);
	// get all AICs:
	final List<Number> aics = util.calculate("AIC");
	final List<Number> rootAICs = util.calculate("${/root/AIC}");
	for (int i=0;i<aics.size();i++) {
	    assertThat("calculation of AIC is not same at index "+i, aics.get(i), equalTo(rootAICs.get(i)));
	}
    }
    
    @Test
    public void fullPathEvaluationWorks(){
	final ListCalculationUtil<IREXP> util = new ListCalculationUtil<IREXP>();
	util.setContent(data);
	//data.get(0).childAt(3).childAt(0).childAt(1).childAt(0).getValue()
	final List<Number> tempEstimates = util.calculate("${/root/coefficients/matrix/Temp/Estimate}");
	assertThat(tempEstimates, notNullNumberInList());
    }
    
    @Test
    public void defaultFinderDoesNotSupportsPostfixMatches() {
	final ListCalculationUtil<IREXP> util = new ListCalculationUtil<IREXP>();
	util.setContent(data);
	final List<Number> tempEstimates = util.calculate("${//Temp/Estimate}");
	assertThat(tempEstimates, not(notNullNumberInList()));
    }
    
    @Test
    public void postfixFinderSupportsPostfixMatches() {
	final ListCalculationUtil<IREXP> util = new ListCalculationUtil<IREXP>();
	util.setNodeFinder(new PostfixNodeFinderImpl<IREXP>());
	util.setContent(data);
	final List<Number> tempEstimates = util.calculate("${//Temp/Estimate}");
	assertThat(tempEstimates, notNullNumberInList());
    }
    
    private Matcher<Iterable<? super Number>> notNullNumberInList() {
	return hasItem(notNullNumber());
    }
    
    private Matcher<Number> notNullNumber() {
	return notNullValue(Number.class);
    }
}
