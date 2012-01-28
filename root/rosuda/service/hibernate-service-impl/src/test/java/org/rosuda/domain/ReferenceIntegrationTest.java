package org.rosuda.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.graph.domain.EntityConverter;
import org.rosuda.graph.domain.Graph;
import org.rosuda.graph.domain.Vertex;
import org.rosuda.graph.service.GraphService;
import org.rosuda.graph.service.search.NameVertexConstraint;
import org.rosuda.graph.service.search.NumberConstraint;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.type.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/spring/hibernate-service-impl.spring.xml",
		"classpath:spring-hibernate-service-test.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public class ReferenceIntegrationTest {

	protected static final Log LOG = LogFactory.getLog(ReferenceIntegrationTest.class);
	private GraphService<Vertex> graphService;
	private SimpleJdbcTemplate jdbcTemplate;
	private int currentNodes;
	private int initialNodeCount;
	private Long pk;

	@Autowired
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	@SuppressWarnings("unchecked")
	@Autowired
	public void setGraphService(final GraphService<?> graphService) {
		this.graphService = (GraphService<Vertex>) graphService;
	}

	@Before
	public void setUp() throws IOException, ClassNotFoundException {
		for (int i=1;i<9;i++) {
			insertRObj("/models/airquality-"+i+".rObj");
		}
		initialNodeCount = jdbcTemplate
				.queryForInt("SELECT count(*) FROM vertex");
		insertRObj("/models/extendedLmSummary.rObj");
	}

	private void insertRObj(String name) throws IOException,
			ClassNotFoundException {
		final ObjectInputStream ois = new ObjectInputStream(
				ReferenceIntegrationTest.class
						.getResourceAsStream(name));
		@SuppressWarnings("unchecked")
		final Node<Vertex> rootNode = (Node<Vertex>) ois.readObject();
		ois.close();
		assertNotNull(rootNode);
		currentNodes = countNodes(rootNode);
		// convert:
		final Graph domainGraph = EntityConverter.convertNodeToGraphEntity
				.apply(rootNode);
		assertNotNull(domainGraph);
		pk = graphService.store(rootNode);
	}
	
	private int countNodes(Node<?> node) {
		int accumulator = 1;
		for (Node<?> child : node.getChildren()) {
			accumulator += countNodes(child);
		}
		return accumulator;
	}
	
	@After
	public void tearDown() {
		graphService.delete(graphService.read(pk));
	}
	
	@Test
	public void testReadByPk() {
		final int storedNodeCount = jdbcTemplate
				.queryForInt("SELECT count(*) FROM vertex");
		assertEquals(currentNodes, storedNodeCount - initialNodeCount);
		// reload:
		final Node<Vertex> reloadedGraph = graphService.read(pk);
		assertNotNull(reloadedGraph);
		final int reloadedNodeCount = countNodes(reloadedGraph);
		assertEquals(currentNodes, reloadedNodeCount);
		// TODO test different finders!

		// delete
		graphService.delete(reloadedGraph);
	}


	@Test
	public void testFindAllGraphs() {
		assertEquals(9, graphService.list().size());
	}
	
	@Test
	public void findOneExtendedLMByConstraintApi() {
				
		final List<VertexConstraint> tempOnly = new ArrayList<VertexConstraint>();
		tempOnly.add(new NameVertexConstraint("Temp").addChildConstraint(new NameVertexConstraint("Pr(>|t|)").addValueConstraint(new NumberConstraint(1e-4, Relation.LT))));
		assertEquals(5, graphService.find(tempOnly).size());

		final List<VertexConstraint> tempUnderCoeffOnly = new ArrayList<VertexConstraint>();
		tempUnderCoeffOnly.add(new NameVertexConstraint("Coefficients").addChildConstraint(new NameVertexConstraint("matrix").addChildConstraint(new NameVertexConstraint("Temp").addChildConstraint(new NameVertexConstraint("Pr(>|t|)").addValueConstraint(new NumberConstraint(1e-4, Relation.LT))))));
		assertEquals(5, graphService.find(tempOnly).size());
		
		final List<VertexConstraint> windConstraint = new ArrayList<VertexConstraint>();
		windConstraint.add(new NameVertexConstraint("Wind").addChildConstraint(new NameVertexConstraint("Pr(>|t|)").addValueConstraint(new NumberConstraint(1e-4, Relation.LT))));
		assertEquals(5, graphService.find(windConstraint).size());
		
		windConstraint.add(new NameVertexConstraint("Temp").addChildConstraint(new NameVertexConstraint("Pr(>|t|)").addValueConstraint(new NumberConstraint(1e-4, Relation.LT))));
		assertEquals(3, graphService.find(windConstraint).size());

		
	}
}
