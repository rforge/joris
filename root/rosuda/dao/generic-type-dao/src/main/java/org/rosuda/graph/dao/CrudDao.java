package org.rosuda.graph.dao;



/**
 * basic operation for any persistent object referred to as entity
 * note that this entity is neither necessarily serializable of a subclass
 * of {@link javax.persistence.Entity}
 * @author ralfseger
 *
 * @param <T>
 */

public interface CrudDao<T, PK> {
	public PK create(final T entity); 
	public T read(final PK key);
	public void update(final T entity);
	public void delete(final T entity);
}
