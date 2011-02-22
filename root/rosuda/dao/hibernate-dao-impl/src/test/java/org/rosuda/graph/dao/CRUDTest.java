package org.rosuda.graph.dao;

import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.graph.domain.Edge;
import org.rosuda.graph.domain.Graph;
import org.rosuda.graph.domain.Value;
import org.rosuda.graph.domain.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-hibernate-dao-test.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class CRUDTest {

	private SimpleJdbcTemplate jdbcTemplate;
	private GraphDao graphDao;

	
	@Autowired
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	@Autowired
	public void setGraphDao(final GraphDao graphDao) {
		this.graphDao = graphDao;
	}
	
//	@After
//	public void cleanUp() {
//		//remove all db entries
//		//currently done by spring-test.xml <prop key="hibernate.hbm2ddl.auto">create-drop</prop>
//	}
	
	/**
	 * test if the table names are present
	 */
	@Test
	public void testSpringConfigDomainModel() {
		Assert.assertNotNull(jdbcTemplate);
		Assert.assertTrue(jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH")>=0);
		Assert.assertTrue(jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX")>=0);
		//Assert.assertTrue(jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH_EDGE")>=0);
		
	}
	
	@Test
	public void testStoreSimpleGraph() throws SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		final Graph domainGraph = new Graph();
		final Vertex root = new Vertex();
		root.setName("root");
		final Vertex valueNode = new Vertex();
		valueNode.setName("answer");
		final Value valueNodeValue = new Value();
		valueNodeValue.setNumber(42);
		valueNode.setValue(valueNodeValue);
		domainGraph.addVertex(root);
		domainGraph.addVertex(valueNode);
		new Edge(root, valueNode, domainGraph);
		
		final long storedPk = graphDao.create(domainGraph);
		Assert.assertEquals(2,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX"));
		Assert.assertEquals(1,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH"));
		Assert.assertEquals(1,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX WHERE TYPE IS NOT NULL"));
		Assert.assertEquals(1,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM EDGE"));

		
		final Graph reloadedGraph = graphDao.read(storedPk);
		Assert.assertNotNull(reloadedGraph);
		
		Assert.assertEquals(1, reloadedGraph.getEdges().size());
		Assert.assertEquals(2, reloadedGraph.getVertices().size());
		
		//check Results (if evrything is saved
		Assert.assertEquals(2,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX"));
		Assert.assertEquals(1,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH"));
		Assert.assertEquals(1,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX WHERE TYPE IS NOT NULL"));
		//nun wo speichert er die ??
		Assert.assertEquals(1,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM EDGE"));
		
		//transactional commits at end, so this test must fail!
		graphDao.delete(domainGraph);
//		
//		Assert.assertEquals(0,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX"));
//		Assert.assertEquals(0,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH"));
//		Assert.assertEquals(0,  jdbcTemplate.queryForInt("SELECT COUNT(*) FROM EDGE"));
	}

	//test circle
}
