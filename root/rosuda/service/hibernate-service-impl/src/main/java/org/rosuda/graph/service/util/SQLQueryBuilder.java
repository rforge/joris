package org.rosuda.graph.service.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.rosuda.graph.service.search.ValueConstraint;
import org.rosuda.graph.service.search.VertexConstraint;

public class SQLQueryBuilder {

	@SuppressWarnings("rawtypes")
	private class MapValueIndexEntry {
		private Class<? extends ValueConstraint> baseClass;
		private Object operator;
		
		public MapValueIndexEntry(Class<? extends ValueConstraint> baseClass,
				Object operator) {
			this.baseClass = baseClass;
			this.operator = operator;
		}

		@Override
		public int hashCode() {
			HashCodeBuilder builder = new HashCodeBuilder();
			builder.append(baseClass);
			builder.append(operator);
			return builder.toHashCode();
		}
	}
	@SuppressWarnings("rawtypes")
	private Map<Class<? extends VertexConstraint>, SQLQueryAppender> classAppenderMapping = Collections.synchronizedMap(new HashMap<Class<? extends VertexConstraint>, SQLQueryAppender>());
	@SuppressWarnings("rawtypes")
	private Map<MapValueIndexEntry, SQLValueQueryAppender> valueAppenderMapping = Collections.synchronizedMap(new HashMap<SQLQueryBuilder.MapValueIndexEntry, SQLValueQueryAppender>());
	
	public Map<String, Object> appendConstraintsToQueryStub(final StringBuilder queryStub, final Iterable<VertexConstraint> vertexConstraints) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (vertexConstraints == null)
			Collections.emptyMap();
		final Map<String, Object> arguments = new TreeMap<String, Object>();
		final StringBuilder whereQueryStub = new StringBuilder();
		appendConstraintsToQueryStub(queryStub, whereQueryStub, vertexConstraints, arguments, null);
		if (whereQueryStub.length() > 0) {
			queryStub.append(whereQueryStub);
		}
		return Collections.unmodifiableMap(arguments);
	}

	@SuppressWarnings(value={"rawtypes","unchecked"})
	void appendConstraintsToQueryStub(final StringBuilder queryStub, final StringBuilder whereQueryStub, final Iterable<VertexConstraint> vertexConstraints, final Map<String, Object> arguments, final String parentId) throws InstantiationException, IllegalAccessException, ClassNotFoundException {		
		for (final VertexConstraint aConstraint : vertexConstraints) {
			SQLQueryAppender builder = getAppender(aConstraint.getClass());
			builder.appendToSqlQuery(this, queryStub, whereQueryStub, arguments, aConstraint, parentId);
		}
	}

	@SuppressWarnings("rawtypes")
	private SQLQueryAppender getAppender(final Class<? extends VertexConstraint> constraintClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (classAppenderMapping.containsKey(constraintClass)) {
			return classAppenderMapping.get(constraintClass);
		} else {
			synchronized (classAppenderMapping) {
				SQLQueryAppender appenderFor = createConstraintConstructor(constraintClass);
				classAppenderMapping.put(constraintClass, appenderFor);
				return appenderFor;
			}
		}
	}
	
	@SuppressWarnings(value={"rawtypes","unchecked"})
	private SQLQueryAppender createConstraintConstructor(final Class<? extends VertexConstraint> constraintClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException {		
		final StringBuilder typeClassBuilder = new StringBuilder(
				SQLQueryBuilder.class.getPackage().getName())
				.append(".").append(
				constraintClass.getSimpleName().replace("Constraint",
						SQLQueryAppender.class.getSimpleName()));
		final Class<? extends SQLQueryAppender> builderClass = (Class<? extends SQLQueryAppender>) Class
				.forName(typeClassBuilder.toString());
		return builderClass.newInstance();
	}

	@SuppressWarnings(value={"rawtypes","unchecked"})
	void appendValueConstraintsToQueryStub(StringBuilder queryStub,
			Iterable<ValueConstraint> valueConstraints,
			Map<String, Object> queryArguments, String parentJoinId) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		for (final ValueConstraint valueConstraint: valueConstraints) {
			SQLValueQueryAppender appender = getAppender(valueConstraint);
			appender.appendToSqlQuery(queryStub, queryArguments, valueConstraint.eval(), parentJoinId);
		}
		
	}

	@SuppressWarnings("rawtypes")
	private SQLValueQueryAppender getAppender(final ValueConstraint valueConstraint) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		final MapValueIndexEntry key = new MapValueIndexEntry(valueConstraint.getClass(), valueConstraint.getOperator());
		if (valueAppenderMapping.containsKey(key)) {
			return valueAppenderMapping.get(key);
		} else {
			final SQLValueQueryAppender appender = createSQLValueQueryAppender(valueConstraint.getClass(), valueConstraint.getOperator());
			valueAppenderMapping.put(key, appender);
			return appender;
		}
	}

	@SuppressWarnings(value={"rawtypes","unchecked"})
	private SQLValueQueryAppender createSQLValueQueryAppender(
			Class<? extends ValueConstraint> constraintClass, Object operator) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		final StringBuilder typeClassBuilder = new StringBuilder(
				SQLQueryBuilder.class.getPackage().getName())
				.append(".").append(operator.toString()).append(
				constraintClass.getSimpleName().replace("Constraint",
						SQLValueQueryAppender.class.getSimpleName()));
		final Class<? extends SQLValueQueryAppender> builderClass = (Class<? extends SQLValueQueryAppender>) Class
				.forName(typeClassBuilder.toString());
		return builderClass.newInstance();
	}
}
