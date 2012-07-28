package org.rosuda.integration;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.domain.util.DebugUtil;
import org.rosuda.graph.service.GraphService;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;
import org.rosuda.util.r.impl.RStarterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-integration-test.xml")
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class ServiceIntegrationTest {

	public static final double EPS = 0.000001;
	//TODO: inject
	private IRConnection connection;
	private ObjectTransformationHandler<Object> handler;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private GraphService<Object> graphService;
	
	private long tick;
	private static final Log log = LogFactory.getLog(ServiceIntegrationTest.class);
	
	@Autowired
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	@Autowired
	public void setGraphService(final GraphService<Object> graphService) {
		this.graphService = graphService;
	}
	
	@Before
	public void setUp() throws Exception {
		// ensure service start
		new RStarterFactory().createService().start();
		connection = new REngineConnectionFactory()
				.createRConnection(new Properties());
		this.handler = new IREXPMapper<Object>().createInstance();
		this.tick = System.currentTimeMillis();
	}

	private void logPerformance(final String action) {
		final long current = System.currentTimeMillis();
		log.info(">>> "+action +" took "+(current-tick)+" ms.");
		tick = System.currentTimeMillis();
	}
	
	@BeforeClass 
	@Transactional
	public static void cleanUpDatabase() throws Exception{
		log.info("cleaning up database");
		final ApplicationContext factory = new ClassPathXmlApplicationContext("spring-integration-test.xml");
		final DataSource dataSource = (DataSource) factory.getBean("dataSource");
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("DELETE FROM graph_edge");
		jdbcTemplate.update("DELETE FROM edge");
		jdbcTemplate.update("DELETE FROM vertex");
		jdbcTemplate.update("DELETE FROM graph");
		log.info("cleaned up database.");
	}
	
	@Test
	public void testStoreTransformedGraph() {
		logPerformance("init");
		connection.voidEval("lm.1<-lm(dist~speed,data=cars)");
		logPerformance("r.eval");
		final IREXP lmREXP = connection.eval("c(summary(lm.1),AIC=AIC(lm.1))");
		logPerformance("ir.eval");
		Assert.assertNotNull(lmREXP);
		final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>()
				.createRoot();
		logPerformance("init.builder");
		handler.transform(lmREXP, lmNode);
		logPerformance("builder.transform");
		final Node<Object> modelObj = lmNode.build();
		logPerformance("builder.build");
		//count nodes:
		final int nodeCount = countNodes(modelObj);
		Assert.assertTrue(nodeCount>15);
		//TODO store model, count nodes
		Assert.assertNotNull(graphService);
		logPerformance("countNodes");
		graphService.store(modelObj);
		logPerformance("store");
		final int storedNodeCount = jdbcTemplate.queryForInt("SELECT count(*) FROM vertex");
		Assert.assertEquals(nodeCount, storedNodeCount); 
		Assert.assertTrue(storedNodeCount % nodeCount == 0); //when no rollback is applied this bean is n times stored
		
		//test Finder
		logPerformance("before.find");
		final List<Node<Object>> entireGraphDB = graphService.find(null);
		logPerformance("after.find");
		Assert.assertNotNull(entireGraphDB);
		Assert.assertEquals(1, entireGraphDB.size());
		final Node<Object> entityFromList = entireGraphDB.get(0);
		Assert.assertNotNull(entityFromList);
		logPerformance("before.count.result");		
		final int reloadedEntityNodeCount = countNodes(entityFromList);
		logPerformance("after.count.result");		
		Assert.assertEquals(nodeCount, reloadedEntityNodeCount);
		//
		DebugUtil.debugSchema(dataSource);
		
		//try to delete
		graphService.delete(entityFromList);
		logPerformance("delete");
		final int currentNodeCount = jdbcTemplate.queryForInt("SELECT count(*) FROM vertex");
		Assert.assertEquals(storedNodeCount, currentNodeCount);
	}
	
	private int countNodes(Node<?> node) {
		int accumulator = 1;
		for (Node<?> child : node.getChildren()) {
			accumulator+=countNodes(child);
		}
		return accumulator;
	}
}
