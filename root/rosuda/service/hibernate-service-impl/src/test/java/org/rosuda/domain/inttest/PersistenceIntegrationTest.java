package org.rosuda.domain.inttest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.domain.util.DebugUtil;
import org.rosuda.graph.domain.GraphWrapper;
import org.rosuda.graph.domain.Vertex;
import org.rosuda.graph.service.GraphService;
import org.rosuda.graph.service.search.NameVertexConstraint;
import org.rosuda.graph.service.search.NumberValueConstraint;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.type.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/***
 * 
 * @author ralfseger
 *	TODO -> extract this with replaceable service as a reference test case!
 *	! this is an integration test not a unit test
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/hibernate-service-impl.spring.xml","classpath:spring-hibernate-service-test.xml"})
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public class PersistenceIntegrationTest {

	private static final String ZWEITES_ZWEIT_ELEMENT = "zweites Zweit-Element";
	private static final String ERSTES_ZWEIT_ELEMENT = "erstes Zweit-Element";
	private static final String ERSTES_ELEMENT = "erstes Element";
	private static ObjectTransformationHandler<Vertex> transformationHandler;
	private static GraphWrapper graph;
	private static Node<Vertex> targetPi;
	private static Node<Vertex> targetE;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private GraphService<Vertex> graphService;

	@Autowired
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	@SuppressWarnings("unchecked")
	@Autowired
	public void setGraphDao(final GraphService<?> graphService) {
		this.graphService = (GraphService<Vertex>) graphService;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<List> createStructureList() {
		final List<List> source = new ArrayList<List>();
		final List<String> list1 = new ArrayList<String>();
		final List list2 = new ArrayList();
		source.add(list1);
		source.add(list2);
		list1.add(ERSTES_ELEMENT);
		list2.add(ERSTES_ZWEIT_ELEMENT);
		list2.add(ZWEITES_ZWEIT_ELEMENT);
		return source;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@BeforeClass
	public static void setUp() throws Exception {
		transformationHandler = new ObjectTransformationHandler<Vertex>();
		graph = new GraphWrapper();
		final Node.Builder<Vertex> targetBuilder = graph.createRoot();
		final List<List> piList = createStructureList();
		piList.get(1).add(Math.PI);
		transformationHandler.transform(piList, targetBuilder);
		targetPi = targetBuilder.build();
		final List<List> eList = createStructureList();
		eList.get(1).add(Math.exp(1.0));
		final Node.Builder<Vertex> targetBuilder2 = graph.createRoot();
		transformationHandler.transform(eList, targetBuilder2);
		targetE = targetBuilder2.build();
	}

	@AfterClass
	public static void tearDown() {
		// TODO reset database state ..
	}

	@Before
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void setUpTestCase() {
		graphService.store(targetPi);
		graphService.store(targetE);
	}
	
	@Test
	public void testGraphsAreStored() {
		final long graphCount = jdbcTemplate
				.queryForLong("SELECT COUNT(*) FROM GRAPH");
		Assert.assertEquals(2, graphCount);
		DebugUtil.debugSchema(dataSource);
	}

	@Test
	public void testFindGraphsThatContainAVertexIdentifiedByName() {
		final List<Map<String, Object>> nodeNames = jdbcTemplate.queryForList("SELECT v.* FROM GRAPH g JOIN VERTEX v ON v.gra_id = g.gra_id");
		nodeNames.get(27).get("NUM");
		nodeNames.get(13).get("NUM");
		//TODO how to cast to Number
		final long graphCount = jdbcTemplate
				.queryForLong("SELECT COUNT(DISTINCT(g.gra_id)) FROM GRAPH g JOIN VERTEX v ON v.gra_id = g.gra_id WHERE v.name = 'iterable'");
		Assert.assertEquals(2, graphCount);
		
		final Collection<VertexConstraint> validConstraint = new ArrayList<VertexConstraint>(); 
		validConstraint.add(new NameVertexConstraint("iterable"));
		assertEquals(2, graphService.find(validConstraint).size());
		final Node<?> n = graphService.find(validConstraint).get(0);
		assertNotNull(n);
		assertTrue(Node.class.isAssignableFrom(n.getChildren().iterator().next().getClass()));
	}

	@Test
	public void testFindGraphsThatContainTwoVerticesIdentifiedByPathName() {		
		Assert.assertEquals(2, getNodesOnTwoElementPathCount("String", "String"));
		Assert.assertEquals(2, getNodesOnTwoElementPathCount("iterable", "String"));
		Assert.assertEquals(0, getNodesOnTwoElementPathCount("String", "iterable"));
		
		
		Collection<VertexConstraint> validConstraint = new ArrayList<VertexConstraint>(); 
		validConstraint.add(new NameVertexConstraint("String").addChildConstraint(new NameVertexConstraint("String")));
		assertEquals(2, graphService.find(validConstraint).size());
		final Node<?> n = graphService.find(validConstraint).get(0);
		assertNotNull(n);
		assertTrue("No match for unordered pair", Node.class.isAssignableFrom(n.getChildren().iterator().next().getClass()));
		
		validConstraint = new ArrayList<VertexConstraint>(); 
		validConstraint.add(new NameVertexConstraint("iterable").addChildConstraint(new NameVertexConstraint("String")));
		assertEquals("Did not find in correct order",2, graphService.find(validConstraint).size());
		
		validConstraint = new ArrayList<VertexConstraint>(); 
		validConstraint.add(new NameVertexConstraint("String").addChildConstraint(new NameVertexConstraint("iterable")));
		assertEquals("Found order violation", 0, graphService.find(validConstraint).size());
	}

	private long getNodesOnTwoElementPathCount(final String child1, final String child2) {
		final long graphCount = jdbcTemplate
				.queryForLong("SELECT COUNT(DISTINCT(g.gra_id)) FROM GRAPH g JOIN VERTEX v ON (v.gra_id = g.gra_id AND v.name = ?)" +
						" JOIN VERTEX v2 ON (v2.gra_id = g.gra_id AND v2.name = ?)" +
						" JOIN EDGE e1 ON (e1.from_id = v.ver_id AND e1.to_id = v2.ver_id)"
						, child1, child2);
		return graphCount;
	}
	
	@Test
	public void testFindGraphThatContainsValue() {
		final long graphCountLess3 = jdbcTemplate
				.queryForLong("SELECT COUNT(DISTINCT(g.gra_id)) FROM GRAPH g JOIN VERTEX v ON v.gra_id = g.gra_id " +
						" JOIN VERTEX v2 ON v2.gra_id = g.gra_id " +
						" WHERE v.string = '"+ERSTES_ELEMENT+"' " +
						" AND v2.num < 3");
		Assert.assertEquals(1, graphCountLess3);
		final Collection<VertexConstraint> validConstraint = new ArrayList<VertexConstraint>(); 
		validConstraint.add(new NameVertexConstraint("Double").addValueConstraint(new NumberValueConstraint(3.0, Relation.GT)).addValueConstraint(new NumberValueConstraint(3.2, Relation.LT)));	
		assertEquals(1, graphService.find(validConstraint).size());
	}
	
}