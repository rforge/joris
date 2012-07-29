package org.rosuda.graph.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.rosuda.graph.dao.GraphDao;
import org.rosuda.graph.domain.EntityConverter;
import org.rosuda.graph.domain.Graph;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.graph.service.util.SQLQueryBuilder;
import org.rosuda.type.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class HibernateGraphServiceImpl<T> implements GraphService<T>{

	private static final Collection<VertexConstraint> EMPTY_LIST = Collections.synchronizedList(new ArrayList<VertexConstraint>());
	@Autowired
	GraphDao graphDao;
	@Autowired
	SessionFactory sessionFactory;
	SQLQueryBuilder queryBuilder;

	public void setQueryBuilder(final SQLQueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	@Override
	//@Transactional(propagation=Propagation.REQUIRED)
	public Long store(Node<T> graph) {
		return graphDao.create(EntityConverter.convertNodeToGraphEntity.apply(graph));
	}

	@Override
	public void delete(Node<T> graph) {
		if (graph instanceof EntityConverter.Entity<?>) {
			@SuppressWarnings("unchecked")
			final EntityConverter.Entity<Graph> entity = (EntityConverter.Entity<Graph>) graph;
			graphDao.delete(entity.getEntity());
		}
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly = true)
	protected Graph readEntity(final Long pk) {
		return graphDao.read(pk);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly = true)
	public Node<T> read(final Long pk) {
		final Graph entity = readEntity(pk);
		return (Node<T>) EntityConverter.convertGraphEntityToNode.apply(entity);
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, readOnly = true)
	public List<Node<T>> find(final Iterable<VertexConstraint> vertexConstraints) {
		
		final Session session = sessionFactory.getCurrentSession();
		final StringBuilder queryBuilderStub = new StringBuilder("SELECT DISTINCT(graph.gra_id) graphid FROM GRAPH graph");
		final Map<String, Object> arguments; 
		try {
			arguments = queryBuilder.appendConstraintsToQueryStub(queryBuilderStub, vertexConstraints);
		} catch (final Exception x) {
			throw new RuntimeException(x);
		}
		final SQLQuery query = session.createSQLQuery(queryBuilderStub.toString().replace("JOIN", "\r\nJOIN").replace("WHERE", "\r\nWHERE"));
		for (final Map.Entry<String, Object> arg : arguments.entrySet()) {
			query.setParameter(arg.getKey(), arg.getValue());
		}
		final List<Long> allGraphsIds = query.addScalar("graphid", StandardBasicTypes.LONG).list();
//		final Criteria criteria = session.createCriteria(Graph.class);	
//		final ProjectionList uniqueCriterion = Projections.projectionList();
//		uniqueCriterion.add(Projections.property("id"));
//		criteria.setProjection(uniqueCriterion);
//		//now we only got the id (PK)
//		translator.addToCriteria(criteria, vertexConstraint);
//		final List<Long> allGraphsIds = criteria.list();
		final List<Node<T>> allGraphs = Lists.transform(allGraphsIds, new Function<Long, Node<T>>() {
			@Override
			public Node<T> apply(final Long pk) {
				return HibernateGraphServiceImpl.this.read(pk);
			}
		});
		return allGraphs;
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly = true)
	@Override
	public List<Node<T>> list() {
		return find(EMPTY_LIST);
	}
}
