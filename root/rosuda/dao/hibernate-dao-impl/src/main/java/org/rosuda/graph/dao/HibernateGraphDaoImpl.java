package org.rosuda.graph.dao;

import org.rosuda.graph.domain.Graph;

public class HibernateGraphDaoImpl extends AbstractHibernateDao<Graph, Long> implements GraphDao{
	
	public HibernateGraphDaoImpl() {
		super(Graph.class);
	}
	
	@Override
	public void delete(final Graph graph) {
		/*final Session currentSession = super.getSession();
		//validate:
		for (final Edge edge: new ArrayList<Edge>(graph.getEdges())) {
			graph.getEdges().remove(edge);
			currentSession.delete(edge);
		}
		for (final Vertex vertex: new ArrayList<Vertex>(graph.getVertices())) {
			graph.getVertices().remove(vertex);
			currentSession.delete(vertex);
		}*/
		super.delete(graph);
	}
}
