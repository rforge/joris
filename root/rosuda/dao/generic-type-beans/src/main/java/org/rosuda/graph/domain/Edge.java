package org.rosuda.graph.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
//@Table(name = "EDGE")
//@IdClass(EdgePk.class)
public class Edge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8146702524453811869L;

	@Id
	@GeneratedValue( strategy=GenerationType.IDENTITY )
	@Column(name="EDG_ID")
	private Long id;
	
	@Column(name = "SUCCESSION")
	private Long succession = Long.MIN_VALUE;

	@OneToOne(cascade={CascadeType.ALL}, orphanRemoval=true)
	@JoinColumn(name = "FROM_ID", nullable = true, insertable=true, updatable=true)
	private Vertex from;

	@OneToOne(cascade={CascadeType.ALL}, orphanRemoval=true)
	@JoinColumn(name = "TO_ID", nullable = true, insertable=true, updatable=true)
	private Vertex to;

	@OneToOne(cascade={CascadeType.ALL}, orphanRemoval=true)
	@JoinColumn(name = "GRAPH_ID", nullable = true, insertable=true, updatable=true)
	private Graph graph;

	public Edge() {
	}

	public Edge(final Vertex from, final Vertex to, final Graph graph) {
		this.from = from;
		this.to = to;
		graph.addEdge(this);
		this.graph = graph;
	}

	//conveniance constructor
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSuccession() {
		return succession;
	}

	public void setSuccession(final Long succession) {
		this.succession = succession;
	}

	public Vertex getFrom() {
		return from;
	}

	public Vertex getTo() {
		return to;
	}

	public void setFrom(final Vertex from) {
		this.from = from;
	}
	
	public void setTo(final Vertex to) {
		this.to = to;
	}
	
	
	public Graph getGraph() {
		return graph;
	}

	public void setGraph(final Graph graph) {
		this.graph = graph;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Edge(from=").append(from).append(", to=").append(to)
				.append(")");
		return sb.toString();
	}
}
