package org.rosuda.domain;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.ObjectInputStream;
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
import org.rosuda.graph.domain.EntityConverter;
import org.rosuda.graph.domain.Graph;
import org.rosuda.graph.domain.GraphWrapper;
import org.rosuda.graph.domain.Vertex;
import org.rosuda.graph.service.GraphService;
import org.rosuda.graph.service.search.NameVertexConstraint;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.type.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
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
	private SimpleJdbcTemplate jdbcTemplate;
	private GraphService<Vertex> graphService;

	@Autowired
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
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
				.queryForLong("SELECT COUNT(DISTINCT(g.gra_id)) FROM GRAPH g JOIN VERTEX v ON v.gra_id = g.gra_id WHERE v.string = '"+ERSTES_ELEMENT+"'");
		Assert.assertEquals(2, graphCount);
		
		final Collection<VertexConstraint> validConstraint = new ArrayList<VertexConstraint>(); 
		validConstraint.add(new NameVertexConstraint(ERSTES_ELEMENT));
		assertEquals(2, graphService.find(validConstraint).size());	
	}

	@Test
	public void testFindGraphsThatContainAVertexIdentifiedByNameWithDistinctSubValue() {
		final List<Map<String, Object>> nodeNames = jdbcTemplate.queryForList("SELECT v.* FROM GRAPH g JOIN VERTEX v ON v.gra_id = g.gra_id");
		final long graphCountAll = jdbcTemplate
				.queryForLong("SELECT COUNT(DISTINCT(g.gra_id)) " +
						" FROM GRAPH g " +
						"	JOIN VERTEX v ON v.gra_id = g.gra_id " +
						" 	JOIN VERTEX v2 ON v2.gra_id = g.gra_id " +
						" WHERE v.string = '"+ERSTES_ELEMENT+"' " +
						" AND v2.num < 4");
		//TODO check Number is stored correctly!
		assertEquals(2, graphCountAll);

		final long graphCountLess3 = jdbcTemplate
				.queryForLong("SELECT COUNT(DISTINCT(g.gra_id)) FROM GRAPH g JOIN VERTEX v ON v.gra_id = g.gra_id " +
						" JOIN VERTEX v2 ON v2.gra_id = g.gra_id " +
						" WHERE v.string = '"+ERSTES_ELEMENT+"' " +
						" AND v2.num < 3");
		Assert.assertEquals(1, graphCountLess3);

		final Collection<VertexConstraint> validConstraint = new ArrayList<VertexConstraint>(); 
		validConstraint.add(new NameVertexConstraint(ERSTES_ELEMENT));
		assertEquals(2, graphService.find(validConstraint).size());	
	}

	
	@Test
	public void testStoreAndDeleteGraph() throws IOException, ClassNotFoundException {
		final int initialNodeCount = jdbcTemplate.queryForInt("SELECT count(*) FROM vertex");
		final ObjectInputStream ois = new ObjectInputStream(
				PersistenceIntegrationTest.class
						.getResourceAsStream("/extendedLmSummary.rObj"));
		@SuppressWarnings("unchecked")
		final Node<Vertex> rootNode = (Node<Vertex>) ois.readObject();
		ois.close();
		Assert.assertNotNull(rootNode);
		final int currentNodes = countNodes(rootNode);
		//convert:
		final Graph domainGraph = EntityConverter.convertNodeToGraphEntity.apply(rootNode);
		Assert.assertNotNull(domainGraph);
		//store:
		final Long pk = graphService.store(rootNode);
		//test if stored:
		final int storedNodeCount = jdbcTemplate.queryForInt("SELECT count(*) FROM vertex");
		Assert.assertEquals(currentNodes, storedNodeCount - initialNodeCount);
		//reload:
		final Node<Vertex> reloadedGraph = graphService.read(pk);
		Assert.assertNotNull(reloadedGraph);
		final int reloadedNodeCount = countNodes(reloadedGraph);
		Assert.assertEquals(currentNodes, reloadedNodeCount);
		//delete
		graphService.delete(reloadedGraph);
		//this may only be tested after flush
		//final int nodeCountAfterDelete = jdbcTemplate.queryForInt("SELECT count(*) FROM vertex");
		//Assert.assertEquals(0, nodeCountAfterDelete);
	}
	
	private int countNodes(Node<?> node) {
		int accumulator = 1;
		for (Node<?> child : node.getChildren()) {
			accumulator+=countNodes(child);
		}
		return accumulator;
	}
}
