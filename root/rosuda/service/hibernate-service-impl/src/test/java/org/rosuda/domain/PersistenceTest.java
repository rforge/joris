package org.rosuda.domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.domain.util.DebugUtil;
import org.rosuda.graph.domain.EntityConverter;
import org.rosuda.graph.domain.Graph;
import org.rosuda.graph.domain.GraphWrapper;
import org.rosuda.graph.domain.Vertex;
import org.rosuda.graph.service.GraphService;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.type.Node;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/hibernate-service-impl.spring.xml","classpath:spring-hibernate-service-test.xml"})
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public class PersistenceTest {

	private static ObjectTransformationHandler<Vertex> transformationHandler;
	private static GraphWrapper graph;
	private static Node.Builder<Vertex> target;
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

	@BeforeClass
	public static void setUp() throws Exception {
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

	@AfterClass
	public static void tearDown() {
		// TODO reset database state ..
	}

	@Ignore
	@Test
	public void testSpringConfig() {
		final BeanFactory factory = new XmlBeanFactory(new ClassPathResource(
				"spring-test.xml"));
		Assert.assertNotNull(factory);
		final DataSource dataSource = (DataSource) factory
				.getBean("dataSource");
		Assert.assertNotNull(dataSource);
		final SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
		Assert.assertNotNull(template);
		// TODO use ORM not template!
	}

	// public void testStoreGraph() throws IOException, ClassNotFoundException {
	// final ObjectInputStream ois = new ObjectInputStream(PersistenceTest.class
	// .getResourceAsStream("/extendedLmSummary.rObj"));
	// final Node<?> rootNode = (Node<?>) ois.readObject();
	// ois.close();
	// //create storable instance
	// //xfinal Graph persistMeGraph = new
	// }

	@Test
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void testStoreGraph() {
		/*
		 * final ApplicationContext factory = new
		 * ClassPathXmlApplicationContext("spring-hibernate-service-test.xml");
		 * Assert.assertNotNull(factory); final DataSource dataSource =
		 * (DataSource) factory .getBean("dataSource");
		 * Assert.assertNotNull(dataSource);
		 */
		Assert.assertNotNull(graphService);
		// graph is an entity so we can save it
		// final Graph entity = graph.getGraph();
		// final GraphService<Vertex> graphService = (GraphService<Vertex>)
		// factory.getBean("graphService");
		graphService.store(target.build());

		// final SimpleJdbcTemplate template = new
		// SimpleJdbcTemplate(dataSource);
		final long graphCount = jdbcTemplate
				.queryForLong("SELECT COUNT(*) FROM GRAPH");
		Assert.assertEquals(1, graphCount);
		DebugUtil.debugSchema(dataSource);
	}

	@Test
	public void testStoreAndDeleteGraph() throws IOException, ClassNotFoundException {
		final ObjectInputStream ois = new ObjectInputStream(
				PersistenceTest.class
						.getResourceAsStream("/extendedLmSummary.rObj"));
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
		Assert.assertEquals(currentNodes, storedNodeCount);
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
