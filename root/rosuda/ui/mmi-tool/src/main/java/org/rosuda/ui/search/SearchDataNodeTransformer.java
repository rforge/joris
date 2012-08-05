package org.rosuda.ui.search;

import java.util.Collection;
import java.util.Collections;

import org.rosuda.graph.service.search.BoolCompareType;
import org.rosuda.graph.service.search.BoolValueConstraint;
import org.rosuda.graph.service.search.NameVertexConstraint;
import org.rosuda.graph.service.search.NumberValueConstraint;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.graph.service.search.StringCompareType;
import org.rosuda.graph.service.search.StringValueConstraint;
import org.rosuda.graph.service.search.ValueConstraint;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;

public class SearchDataNodeTransformer {

	public Collection<VertexConstraint> transform(SearchDataNode root) {
		final VertexConstraint vc = toVertexConstraint(root);
		if (vc == null)
			return Collections.emptyList();
		return Collections.singletonList(vc);
	}

	private VertexConstraint toVertexConstraint(final SearchDataNode node) {
		if (ConstraintType.Name.equals(node.getType())) {
			final NameVertexConstraint nvc = new NameVertexConstraint(
					node.getName());
			for (SearchDataNode child : node.getChildren()) {
				VertexConstraint vertexConstraint = toVertexConstraint(child);
				if (vertexConstraint != null)
					nvc.addChildConstraint(vertexConstraint);
				else {
					@SuppressWarnings("rawtypes")
					final ValueConstraint valueConstraint = toValueConstraint(child);
					if (valueConstraint != null)
						nvc.addValueConstraint(valueConstraint);
				}

			}
			return nvc;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private ValueConstraint toValueConstraint(SearchDataNode child) {
		switch (child.getType()) {
		case Boolean:
			return new BoolValueConstraint(child.isBool(),
					(BoolCompareType) child.getTypeValue());
		case Number:
			return new NumberValueConstraint(child.getNumber(),
					(Relation) child.getTypeValue());
		case String:
			return new StringValueConstraint(child.getString(),
					(StringCompareType) child.getTypeValue());
		default:
			return null;
		}
	}
}
