package org.rosuda.domain.inttest;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.domain.util.DebugUtil;
import org.rosuda.graph.domain.Edge;
import org.rosuda.graph.domain.Graph;
import org.rosuda.graph.domain.Value;
import org.rosuda.graph.domain.Vertex;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-beans-test.xml")
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class SpringWiringIntegrationTest implements ApplicationContextAware {

    private SessionFactory sessionFactory;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	this.sessionFactory = (SessionFactory) applicationContext.getBean("hibernateSessionFactory");
    }

    @Autowired
    public void setDataSource(final DataSource dataSource) {
	this.dataSource = dataSource;
	this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Ignore
    @Test
    public void testSpringConfigDomainModel() {
	/*
	 * final XmlBeanFactory factory = new XmlBeanFactory(new
	 * ClassPathResource("spring-test.xml")); PropertyPlaceholderConfigurer
	 * cfg = new PropertyPlaceholderConfigurer(); cfg.setLocation(new
	 * ClassPathResource("spring/jdbc.properties"));
	 * cfg.postProcessBeanFactory(factory);
	 */
	final ApplicationContext factory = new FileSystemXmlApplicationContext("classpath:spring-test.xml");
	Assert.assertNotNull(factory);
	final DataSource dataSource = (DataSource) factory.getBean("dataSource");
	Assert.assertNotNull(dataSource);
	final JdbcTemplate template = new JdbcTemplate(dataSource);
	Assert.assertNotNull(template);

	// DEBUG output schema ?
	Assert.assertEquals(0, template.queryForInt("SELECT COUNT(*) FROM VERTEX"));
	Assert.assertEquals(0, template.queryForInt("SELECT COUNT(*) FROM EDGE"));
	Assert.assertEquals(0, template.queryForInt("SELECT COUNT(*) FROM GRAPH"));
	Assert.assertEquals(0, template.queryForInt("SELECT COUNT(*) FROM BDATA"));

	DebugUtil.debugSchema(dataSource);

	Assert.assertNotNull(dataSource);
    }

    @Test
    public void testStoreDomainGraph() {
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
	// store
	final Session session = sessionFactory.openSession();
	final Transaction tx = (Transaction) session.getTransaction();
	tx.begin();
	session.save(domainGraph);
	session.flush();
	tx.commit();
	session.close();

	// final long storedPk = graphDao.create(domainGraph);
	Assert.assertEquals(2, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX"));
	Assert.assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH"));
	Assert.assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VERTEX WHERE TYPE IS NOT NULL"));
	Assert.assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM EDGE"));

    }

    // @Ignore
    // @Test
    // public void testBinaryContentWriteStream() throws IOException {
    // final BinaryContent container = new BinaryContent();
    // Assert.assertNull(container.getData());
    // Assert.assertNotNull(container.getDataFromStream());
    // final ClassPathResource springResource = new
    // ClassPathResource("spring-test.xml");
    // final long length = springResource.contentLength();
    // container.setDataFromStream(springResource.getInputStream());
    // Assert.assertEquals(length, container.getData().length);
    // }
}
