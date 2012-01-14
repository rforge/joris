package org.rosuda.graph.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.rosuda.graph.dao.GraphDao;
import org.rosuda.graph.domain.EntityConverter;
import org.rosuda.graph.domain.Graph;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.type.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class HibernateGraphServiceImpl<T> implements GraphService<T>{

	@Autowired
	GraphDao graphDao;
	@Autowired
	SessionFactory sessionFactory;
	VertexConstraintToCriteriaTranslator translator;

	public void setVertexConstraintToCriteriaTranslator(final VertexConstraintToCriteriaTranslator translator) {
		this.translator = translator;
	}

	@Override
	//@Transactional(propagation=Propagation.REQUIRED)
	public Long store(Node<T> graph) {
		return graphDao.create(EntityConverter.convertNodeToGraphEntity.apply(graph));
	}

	@Override
	public void delete(Node<T> graph) {
		if (graph instanceof EntityConverter.Entity<?>) {
			final EntityConverter.Entity<Graph> entity = (EntityConverter.Entity<Graph>) graph;
			graphDao.delete(entity.getEntity());
		}
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly = true)
	protected Graph readEntity(final Long pk) {
		return graphDao.read(pk);
	}
	
	@Override
	public Node<T> read(final Long pk) {
		final Graph entity = readEntity(pk);
		return (Node<T>) EntityConverter.convertGraphEntityToNode.apply(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly = true)
	public List<Node<T>> find(final Collection<VertexConstraint> vertexConstraint) {
		
		final Session session = sessionFactory.getCurrentSession();
		final Criteria criteria = session.createCriteria(Graph.class);	
		final ProjectionList uniqueCriterion = Projections.projectionList();
		uniqueCriterion.add(Projections.property("id"));
		criteria.setProjection(uniqueCriterion);
		//now we only got the id (PK)
		if (vertexConstraint != null) {
			int i = 1;
			for (final VertexConstraint constraint: vertexConstraint) {
				translator.toCriteria(criteria, constraint, i++);
			}
		}
		final List<Long> allGraphsIds = criteria.list();
		final List<Node<T>> allGraphs = Lists.transform(allGraphsIds, new Function<Long, Node<T>>() {
			@Override
			public Node<T> apply(final Long pk) {
				return HibernateGraphServiceImpl.this.read(pk);
			}
		});
		return allGraphs;
	}

}
