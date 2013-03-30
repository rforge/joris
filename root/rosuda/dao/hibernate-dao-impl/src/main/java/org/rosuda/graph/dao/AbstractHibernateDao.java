package org.rosuda.graph.dao;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class AbstractHibernateDao<T, PK extends Serializable> implements CrudDao<T, PK> {

    private SessionFactory sessionFactory;
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateDao.class);

    private Class<T> type;

    public AbstractHibernateDao(Class<T> type) {
        this.type = type;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Autowired
    @Required
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public PK create(T o) {
        return (PK) getSession().save(o);
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public T read(PK id) {
        return (T) getSession().get(type, id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void update(T o) {
        getSession().update(o);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(T o) {
        getSession().delete(o);
    }

}
