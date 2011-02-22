package org.rosuda.graph.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "VERTEX")
public class Vertex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2385917662819934506L;

	//use one simple primary key - another impl could use a PK class
	@Id
	@GeneratedValue( strategy=GenerationType.IDENTITY )
	@Column(name = "VER_ID")
	private Long id;

	@ManyToOne(cascade={CascadeType.MERGE, CascadeType.REMOVE})
	@JoinColumn(name = "GRA_ID", nullable = false)
	private Graph graph;

	//payload:
	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "VALUE")
	private Value value;

	public Vertex() {
	}

	public Vertex(final Graph graph, final Long vertexId) {
		this.graph = graph;
//		this.graphId = graph.getId();
		this.id = vertexId;
	}
	
	public Vertex(final Value value) {
		this.value = value;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(final Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Vertex(name=\"").append(name).append("\",id=\"").append(id)
				.append("\",value=");
		sb.append(value);
		sb.append(")");
		return sb.toString();
	}
}
