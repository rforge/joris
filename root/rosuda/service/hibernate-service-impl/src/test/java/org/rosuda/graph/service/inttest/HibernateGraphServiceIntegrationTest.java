package org.rosuda.graph.service.inttest;

import org.junit.runner.RunWith;
import org.rosuda.graph.service.search.ConstrainedSearchBaseTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/hibernate-service-impl.spring.xml","classpath:spring-hibernate-service-test.xml"})
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public class HibernateGraphServiceIntegrationTest extends ConstrainedSearchBaseTest{

}
