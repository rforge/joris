package org.rosuda.graph.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.rosuda.graph.service.search.NameVertexConstraint;
import org.rosuda.graph.service.search.VertexConstraint;

public class VertexConstraintToCriteriaTranslator {

	private final Map<Class<? extends VertexConstraint>, CriteriaBuilder> map = Collections
			.synchronizedMap(new HashMap<Class<? extends VertexConstraint>, CriteriaBuilder>());

	public VertexConstraintToCriteriaTranslator() {

	}

	interface CriteriaBuilder<T extends VertexConstraint> {
		Criteria toCriteria(final Criteria parentCriterion, final T constraint, final int number);
	}

	public Criteria toCriteria(final Criteria parentCriterion, final VertexConstraint constraint, final int numberOfCriteria) {
		if (!map.containsKey(constraint)) {
			synchronized (map) {
				CriteriaBuilder builder;
				try {
					builder = makeCriteriaBuilderForClass(constraint.getClass());
				} catch (final Exception x) {
					throw new RuntimeException(
							"could not create CriteriaBuilder for type "
									+ constraint, x);
				}
				map.put(constraint.getClass(), builder);
			}
		}
		return map.get(constraint.getClass()).toCriteria(parentCriterion, constraint, numberOfCriteria);
	}

	private CriteriaBuilder makeCriteriaBuilderForClass(
			Class<? extends VertexConstraint> constraintClass)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		final StringBuilder innerClassBuilder = new StringBuilder(
				VertexConstraintToCriteriaTranslator.class.getName()).append(
				"$").append(
				constraintClass.getSimpleName().replace("Constraint",
						"CriteriaBuilder"));
		final Class<? extends CriteriaBuilder> builderClass = (Class<? extends CriteriaBuilder>) Class
				.forName(innerClassBuilder.toString());
		final Constructor<? extends CriteriaBuilder> constructor = builderClass.getConstructor(this.getClass());
		return constructor.newInstance(this);
	}

	public class NameVertexCriteriaBuilder implements
			CriteriaBuilder<NameVertexConstraint> {

		@Override
		public Criteria toCriteria(final Criteria parentCriterion, final NameVertexConstraint constraint, final int number) {	
			return parentCriterion.createCriteria("vertices", "v"+number).add(Restrictions.ilike("value.string", constraint.getName()));
		}

	}
}
