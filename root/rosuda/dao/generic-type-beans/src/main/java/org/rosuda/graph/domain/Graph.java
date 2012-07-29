package org.rosuda.graph.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table( name = "GRAPH" )
public class Graph{

	@Id 
	@GeneratedValue( strategy=GenerationType.IDENTITY )
	@Column(name="GRA_ID")
	private Long id;// = UUID.randomUUID().node();

	@MapKey(name = "vertexId")
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, mappedBy = "graph", orphanRemoval=true)
	//private final Map<Long, Vertex> vertices = new HashMap<Long, Vertex>();
	private final Set<Vertex> vertices = new HashSet<Vertex>();

	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, mappedBy = "graph", orphanRemoval=true)
	@Column(name = "EDGE")
	private final List<Edge> edges = new ArrayList<Edge>();
				
	//bean methods
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	protected void addEdge(final Edge edge) {
		this.edges.add(edge);
	}
	
	public Collection<Vertex> getVertices() {
		return vertices;
	}

	public Collection<Edge> getEdges() {
		return edges;
	}

	public void setVertices(final List<Vertex> vertices) {
		this.vertices.clear();
		if (vertices !=null)
			this.vertices.addAll(vertices);
	}

	public void setEdges(final List<Edge> edges) {
		this.edges.clear();
		if (edges !=null)
			this.edges.addAll(edges);
	}

	@Override
	public String toString() {
		final StringBuilder tsb = new StringBuilder(Graph.class.getSimpleName()).append(".id=").append(this.id).append(",vertices={\n");
		for (final Vertex v : vertices) {
			tsb.append(v).append("\n");
		}
		tsb.append("}");
		for (final Edge edge: edges) {
			tsb.append(edge).append("\n");
		}
		tsb.append(")");
		return tsb.toString();
	}
	
	public void addVertex(final Vertex vertex) {
		vertex.setGraph(this);
		this.vertices.add(vertex);
	}

	
//	public void link(final Vertex from, final Vertex to) {
//		addVertex(from);
//		addVertex(to);
//		
//		final List<Long> links;
//		if (edges.containsKey(from.getId())) {
//			links = edges.get(from.getId());
//		} else {
//			links = new ArrayList<Long>();
//			this.edges.put(from.getId(), links);
//		}
//		links.add(to.getId());
//	}
	
//	public Vertex findByName(final String vertexName) {
//		for (final Vertex vertex : vertices) {
//			if (vertexName.equals(vertex.getName())) {
//				return vertex;
//			}
//		}
//		return null;
//	}
}
