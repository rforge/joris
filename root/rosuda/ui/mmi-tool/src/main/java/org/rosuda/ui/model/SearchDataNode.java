package org.rosuda.ui.model;

import java.math.BigDecimal;
import java.util.List;

import org.rosuda.graph.service.search.BoolCompareType;
import org.rosuda.graph.service.search.NameType;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.graph.service.search.StringCompareType;

import com.google.common.collect.Lists;

public class SearchDataNode {

	public enum ConstraintType {
		Name(NameType.class)
		,String(StringCompareType.class)
		, Number(Relation.class)
		, Boolean(BoolCompareType.class)
		;

		private final Class<? extends Enum<?>> relationType;
		
		ConstraintType(final Class<? extends Enum<?>> enumType) {
			this.relationType = enumType;
		}
		
		Class<? extends Enum<?>> getRelationType() {
			return relationType;
		}
	}
	
	private String name;
	private ConstraintType type;
	private Enum<?> typeValue;
	private BigDecimal number;
	private String string;
	private boolean bool;
	
	public SearchDataNode(final String name, final ConstraintType type) {
		this.name = name;
		this.type = type;
	}
	
	private List<SearchDataNode> children = Lists.newArrayList();

	public List<SearchDataNode> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public SearchDataNode setName(String name) {
		this.name = name;
		return this;
	}

	public ConstraintType getType() {
		return type;
	}

	public SearchDataNode setType(ConstraintType type) {
		this.type = type;
		return this;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public SearchDataNode setNumber(BigDecimal number) {
		this.number = number;
		return this;
	}

	public String getString() {
		return string;
	}

	public SearchDataNode setString(String string) {
		this.string = string;
		return this;
	}

	public boolean isBool() {
		return bool;
	}

	public SearchDataNode setBool(boolean bool) {
		this.bool = bool;
		return this;
	}

	public SearchDataNode setChildren(List<SearchDataNode> children) {
		this.children = children;
		return this;
	}

	public Object getConstaintValue() {
		switch (type) {
			case Boolean: return isBool();
			case Number: return getNumber();
			case String: return getString();
			default: return null;
		}
	}

	public Enum<?> getTypeValue() {
		return typeValue;
	}
	
	public SearchDataNode setTypeValue(Enum<?> typeValue) {
		this.typeValue = typeValue;
		return this;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(name).toString();
	}

	public SearchDataNode addChild(final SearchDataNode child) {
		children.add(child);
		return this;
	}

}
