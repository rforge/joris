package org.rosuda.integration.spring;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.graph.service.GraphService;
import org.rosuda.integration.suites.util.AfterPlainJavaConnectionIntegrationTestSuite;
import org.rosuda.integration.suites.util.BeforePlainJavaConnectionIntegrationTestSuite;
import org.rosuda.integration.suites.util.PlainJavaConnectionTestSuiteContext;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;
import org.rosuda.util.java.RServeUtil;
import org.rosuda.util.process.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private IRConnection connection;
    private ObjectTransformationHandler<Object> handler;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private GraphService<Object> graphService;

    private long tick;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceIntegrationTest.class);

    @Autowired
    public void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    public static void cleanupRServe() {
        if (OS.isWindows()) {
            RServeUtil.killAllWindowsRProcesses();
        } else {
            RServeUtil.killAllUXRProcesses();
        }
        BeforePlainJavaConnectionIntegrationTestSuite.setupAll();
    }

    @AfterClass
    public static void releaseContext() throws Exception {
        AfterPlainJavaConnectionIntegrationTestSuite.tearDown();
    }

    @Autowired
    public void setGraphService(final GraphService<Object> graphService) {
        this.graphService = graphService;
    }

    @Before
    public void setUp() throws Exception {
        connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection();
        this.handler = new IREXPMapper<Object>().createInstance();
        this.tick = System.currentTimeMillis();
    }

    private void logPerformance(final String action) {
        final long current = System.currentTimeMillis();
        LOGGER.info(">>> " + action + " took " + (current - tick) + " ms.");
        tick = System.currentTimeMillis();
    }

    @BeforeClass
    @Transactional
    public static void cleanUpDatabase() throws Exception {
        LOGGER.info("cleaning up database");
        PlainJavaConnectionTestSuiteContext.getInstance().shutdown();
        final ApplicationContext factory = new ClassPathXmlApplicationContext("spring-integration-test.xml");
        final DataSource dataSource = (DataSource) factory.getBean("dataSource");
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // find tables
        Connection dbConnection = dataSource.getConnection();
        ResultSet tableQuery = dbConnection.getMetaData().getTables(null, null, "%", null);
        final Set<String> tableNames = new HashSet<String>();
        while (tableQuery.next()) {
            tableNames.add(tableQuery.getString(3).toLowerCase());
        }
        final List<String> deleteTables = Arrays.asList("graph_edge", "edge", "vertex", "graph");
        for (final String deleteTable : deleteTables) {
            if (tableNames.contains(deleteTable)) {
                jdbcTemplate.update("DELETE FROM " + deleteTable);
                LOGGER.debug("trucated table " + deleteTable);
            }
        }
        LOGGER.info("cleaned up database.");
    }

    @Test
    public void testStoreTransformedGraph() {
        logPerformance("init");
        connection.voidEval("lm.1<-lm(dist~speed,data=cars)");
        logPerformance("r.eval");
        final IREXP lmREXP = connection.eval("c(summary(lm.1),AIC=AIC(lm.1))");
        logPerformance("ir.eval");
        Assert.assertNotNull(lmREXP);
        final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
        logPerformance("init.builder");
        handler.transform(lmREXP, lmNode);
        logPerformance("builder.transform");
        final Node<Object> modelObj = lmNode.build();
        logPerformance("builder.build");
        // count nodes:
        final int nodeCount = countNodes(modelObj);
        Assert.assertTrue(nodeCount > 15);
        // TODO store model, count nodes
        Assert.assertNotNull(graphService);
        logPerformance("countNodes");
        graphService.store(modelObj);
        logPerformance("store");
        final int storedNodeCount = jdbcTemplate.queryForInt("SELECT count(*) FROM vertex");
        Assert.assertEquals(nodeCount, storedNodeCount);
        Assert.assertTrue(storedNodeCount % nodeCount == 0); // when no rollback
                                                             // is applied this
                                                             // bean is n times
                                                             // stored

        // test Finder
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
        // DebugUtil.debugSchema(dataSource);

        // try to delete
        graphService.delete(entityFromList);
        logPerformance("delete");
        final int currentNodeCount = jdbcTemplate.queryForInt("SELECT count(*) FROM vertex");
        Assert.assertEquals(storedNodeCount, currentNodeCount);
    }

    private int countNodes(Node<?> node) {
        int accumulator = 1;
        for (Node<?> child : node.getChildren()) {
            accumulator += countNodes(child);
        }
        return accumulator;
    }
}
