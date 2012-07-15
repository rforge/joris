package org.rosuda.graph.service.search;

import org.rosuda.type.Value;

public interface ValueConstraintEvaluator<T, OP> {

	public boolean matches(final ValueConstraint<T, OP> vc, Value value);
	
	@SuppressWarnings("rawtypes")
	public class Impl implements ValueConstraintEvaluator {

		private ValueConstraintEvaluator numberEvaluator = new ValueConstraintEvaluator<Number, Relation>() {

			@Override
			public boolean matches(ValueConstraint<Number, Relation> vc, Value value) {
				final Number num = value.getNumber();
				if (num == null)
					return false;
				final NumberValueConstraint numberConstraint = (NumberValueConstraint) vc;
				switch (numberConstraint.getOperator()) {
					case GT: 
						return num.doubleValue() > numberConstraint.getValue().doubleValue();
					case GE: 
						return num.doubleValue() >= numberConstraint.getValue().doubleValue();
					case EQ: 
						return num.doubleValue() == numberConstraint.getValue().doubleValue();
					case LE: 
						return num.doubleValue() <= numberConstraint.getValue().doubleValue();
					case LT: 
						return num.doubleValue() < numberConstraint.getValue().doubleValue();
					
				}
				return true;
			}
			
		};
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean matches(final ValueConstraint vc, Value value) {
			if (value == null)
				return false;
			switch (vc.getType()) {
			case NUMBER:
				return numberEvaluator.matches(vc, value);
			default:
				break;
			}
			return false;
		}
		
	}
}
