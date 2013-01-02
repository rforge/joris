package org.rosuda.ui.search;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.rosuda.graph.service.search.Relation;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;
import org.rosuda.ui.test.ModelInitializer;

public class SearchDialogTestModelData extends ModelInitializer<SearchDialogModel>{

    @Override
    protected void initModel(SearchDialogModel model) {
	final SearchTreeModel searchTreeModel = new SearchTreeModel();
	final SearchDataNode rootNode = new SearchDataNode("Root", ConstraintType.Name);
	searchTreeModel.setRoot(rootNode);
	final SearchDataNode coefficients = new SearchDataNode("coefficients", ConstraintType.Name);
	rootNode.addChild(coefficients);
	final SearchDataNode matrix = new SearchDataNode("matrix", ConstraintType.Name);
	coefficients.addChild(matrix);
	final SearchDataNode distNode = new SearchDataNode("dist", ConstraintType.Name);
	matrix.addChild(distNode);

	final MathContext precion2 = new MathContext(2, RoundingMode.HALF_UP);
	distNode.addChild(new SearchDataNode("Estimate", ConstraintType.Name).addChild(new SearchDataNode(null, ConstraintType.Number).setTypeValue(Relation.GT).setNumber(BigDecimal.ZERO)));
	distNode.addChild(new SearchDataNode("Estimate", ConstraintType.Name).addChild(new SearchDataNode(null, ConstraintType.Number).setTypeValue(Relation.LT).setNumber(new BigDecimal(10, precion2))));

	distNode.addChild(new SearchDataNode("Pr(>|t|)", ConstraintType.Name).addChild(new SearchDataNode(null, ConstraintType.Number).setTypeValue(Relation.LT).setNumber(new BigDecimal(0.15, precion2))));
	//TODO set model content
	model.setSearchTreeModel(searchTreeModel);
    }

}
