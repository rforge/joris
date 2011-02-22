package org.rosuda.visualizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Configuration implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3306191000345662176L;
	private String triggerElement;
	private String qualityElement;
	private String complexityElement;
	private List<String> filterElements = new ArrayList<String>();
	
	public String getTriggerElement() {
		return triggerElement;
	}
	public void setTriggerElement(String triggerElement) {
		this.triggerElement = triggerElement;
	}
	public String getQualityElement() {
		return qualityElement;
	}
	public void setQualityElement(String qualityElement) {
		this.qualityElement = qualityElement;
	}
	public String getComplexityElement() {
		return complexityElement;
	}
	public void setComplexityElement(String complexityElement) {
		this.complexityElement = complexityElement;
	}
	
	public void addFilterElement(final String value) {
		this.filterElements.add(value);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Configuration(");
			builder.append("triggerElement=\"").append(triggerElement).append("\"");
			builder.append(", qualityElement=\"").append(qualityElement).append("\"");
			builder.append(", complexityElement=\"").append(complexityElement).append("\"");
			builder.append(", filteredElements=\"").append(filterElements).append("\"");
			
		builder.append(")");
		return builder.toString();
	}
}
