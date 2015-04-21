package com.harrywu.springweb.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class CustomHibernateTemplate implements CustomHibernateOperations {
    
    private SessionFactory sessionFactory;
    private boolean cacheQueries = false;
    private boolean allowCreate = true;
    private String queryCacheRegion;
    private int fetchSize = 0;

    public CustomHibernateTemplate() {
    }

    public CustomHibernateTemplate(SessionFactory sessionFactory) {
        setSessionFactory(sessionFactory);
        afterPropertiesSet();
    }

    public CustomHibernateTemplate(SessionFactory sessionFactory, boolean allowCreate) {
        setSessionFactory(sessionFactory);
        setAllowCreate(allowCreate);
        afterPropertiesSet();
    }

    public static void applyTransactionTimeout(Query query, SessionFactory sessionFactory) {
        Assert.notNull(query, "No Query object specified");
        if (sessionFactory != null) {
            SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
            if (sessionHolder != null && sessionHolder.hasTimeout()) {
                query.setTimeout(sessionHolder.getTimeToLiveInSeconds());
            }
        }
    }

    public static void applyTransactionTimeout(Criteria criteria, SessionFactory sessionFactory) {
        Assert.notNull(criteria, "No Criteria object specified");
        SessionHolder sessionHolder =
            (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
        if (sessionHolder != null && sessionHolder.hasTimeout()) {
            criteria.setTimeout(sessionHolder.getTimeToLiveInSeconds());
        }
    }

    public void afterPropertiesSet() {
        if (getSessionFactory() == null) {
            throw new IllegalArgumentException("Property 'sessionFactory' is required");
        }
    }

    public Serializable save(final Object entity) throws DataAccessException {
        return sessionFactory.getCurrentSession().save(entity);
    }

    public void update(final Object entity) throws DataAccessException {
        sessionFactory.getCurrentSession().update(entity);
    }

    public void delete(Object entity) throws DataAccessException {
        delete(entity, null);
    }

    public void delete(final Object entity, final LockOptions lockOptions) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        if (lockOptions != null) {
            session.buildLockRequest(lockOptions).lock(entity);
        }
        session.delete(entity);
    }

    public void delete(String entityName, Object entity) throws DataAccessException {
        delete(entityName, entity, null);
    }

    public void delete(final String entityName, final Object entity, final LockOptions lockOptions) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        if (lockOptions != null) {
            session.buildLockRequest(lockOptions).lock(entity);
        }
        session.delete(entityName, entity);
    }

    public void saveOrUpdate(final Object entity) throws DataAccessException {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @SuppressWarnings("unchecked")
    public <T> T merge(final T entity) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        return (T) session.merge(entity);
    }

    @SuppressWarnings("unchecked")
    public <T> T merge(final String entityName, final T entity) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        return (T) session.merge(entityName, entity);
    }
    
    public <T> T get(Class<T> entityClass, Serializable id) throws DataAccessException {
        return get(entityClass, id, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> entityClass, final Serializable id, final LockOptions lockOptions) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        if (lockOptions != null)
            return (T) session.get(entityClass, id, lockOptions);
        return (T) session.get(entityClass, id);
    }

    public Object get(String entityName, Serializable id) throws DataAccessException {
        return get(entityName, id, null);
    }

    public Object get(final String entityName, final Serializable id, final LockOptions lockOptions) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        if (lockOptions != null)
            return session.get(entityName, id, lockOptions);
        return session.get(entityName, id);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public List find(final String queryString, final Object[] values,
                     final int firstResult, final int maxResults) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.createQuery(queryString);
        prepareQuery(queryObject, firstResult, maxResults);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                queryObject.setParameter(i, values[i]);
            }
        }
        return queryObject.list();
    }
    
    @SuppressWarnings("rawtypes")
    public List findByCriteria(DetachedCriteria criteria) throws DataAccessException {
        return findByCriteria(criteria, -1, -1);
    }

    @SuppressWarnings("rawtypes")
    public List findByCriteria(final DetachedCriteria criteria, final int firstResult, final int maxResults) throws DataAccessException {
        Assert.notNull(criteria, "DetachedCriteria must not be null");
        Session session = sessionFactory.getCurrentSession();
        Criteria executableCriteria = criteria.getExecutableCriteria(session);
        prepareCriteria(executableCriteria, firstResult, maxResults);
        return executableCriteria.list();
    }
    
    @SuppressWarnings("rawtypes")
    public List findByExample(Object exampleEntity) throws DataAccessException {
        return findByExample(null, exampleEntity, -1, -1);
    }

    @SuppressWarnings("rawtypes")
    public List findByExample(String entityName, Object exampleEntity) throws DataAccessException {
        return findByExample(entityName, exampleEntity, -1, -1);
    }

    @SuppressWarnings("rawtypes")
    public List findByExample(Object exampleEntity, int firstResult, int maxResults) throws DataAccessException {
        return findByExample(null, exampleEntity, firstResult, maxResults);
    }

    @SuppressWarnings("rawtypes")
    public List findByExample(final String entityName, final Object exampleEntity, final int firstResult, final int maxResults) throws DataAccessException {
        Assert.notNull(exampleEntity, "Example entity must not be null");
        Session session = sessionFactory.getCurrentSession();
        Criteria executableCriteria = (entityName != null ?
                session.createCriteria(entityName) : session.createCriteria(exampleEntity.getClass()));
        executableCriteria.add(Example.create(exampleEntity));
        prepareCriteria(executableCriteria, firstResult, maxResults);
        return executableCriteria.list();
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public List findByNamedParam(final String queryString, final String[] paramNames, final Object[] values,
                                 final int firstResult, final int maxResults) throws DataAccessException {
        if (paramNames != null && values != null && paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.createQuery(queryString);
        prepareQuery(queryObject, firstResult, maxResults);
        if (paramNames != null && values != null)
            for (int i = 0; i < values.length; i++)
                applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
        return queryObject.list();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List findByValueBean(final String queryString, final Object valueBean,
                                final int firstResult, final int maxResults) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.createQuery(queryString);
        prepareQuery(queryObject, firstResult, maxResults);
        queryObject.setProperties(valueBean);
        return queryObject.list();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List findByNamedQuery(final String queryName, final Object[] values,
                                 final int firstResult, final int maxResults) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.getNamedQuery(queryName);
        prepareQuery(queryObject, firstResult, maxResults);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                queryObject.setParameter(i, values[i]);
            }
        }
        return queryObject.list();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List findByNamedQueryAndNamedParam(final String queryName, final String[] paramNames, final Object[] values,
                                              final int firstResult, final int maxResults) throws DataAccessException {
        if (paramNames != null && values != null && paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.getNamedQuery(queryName);
        prepareQuery(queryObject, firstResult, maxResults);
        if (paramNames != null && values != null)
            for (int i = 0; i < values.length; i++)
                applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
        return queryObject.list();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List findByNamedQueryAndValueBean(final String queryName, final Object valueBean,
                                             final int firstResult, final int maxResults) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.getNamedQuery(queryName);
        prepareQuery(queryObject, firstResult, maxResults);
        queryObject.setProperties(valueBean);
        return queryObject.list();
    }
    
    public void flush() throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        session.flush();
    }
    
    public void clear() throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        session.clear();
    }
    
    public int bulkUpdate(String queryString) throws DataAccessException {
        return bulkUpdate(queryString, (Object[]) null);
    }

    public int bulkUpdate(String queryString, Object value) throws DataAccessException {
        return bulkUpdate(queryString, new Object[] {value});
    }

    public int bulkUpdate(final String queryString, final Object... values) throws DataAccessException {
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.createQuery(queryString);
        prepareQuery(queryObject);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                queryObject.setParameter(i, values[i]);
            }
        }
        return queryObject.executeUpdate();
    }

    public int bulkUpdate(final String queryString, final String[] paramNames, final Object[] values) throws DataAccessException {
        if (paramNames != null && values != null && paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.createQuery(queryString);
        prepareQuery(queryObject);
        if (paramNames != null && values != null)
            for (int i = 0; i < values.length; i++)
                queryObject.setParameter(paramNames[i], values[i]);
        return queryObject.executeUpdate();
    }

    protected void prepareQuery(Query queryObject) {
        prepareQuery(queryObject, -1, -1);
    }

    protected void prepareQuery(Query queryObject, final int firstResult, final int maxResults) {
        if (isCacheQueries()) {
            queryObject.setCacheable(true);
            if (getQueryCacheRegion() != null) {
                queryObject.setCacheRegion(getQueryCacheRegion());
            }
        }

        applyTransactionTimeout(queryObject, getSessionFactory());

        if (firstResult >= 0)
            queryObject.setFirstResult(firstResult);
        if (maxResults > 0) {
            queryObject.setMaxResults(maxResults);
            queryObject.setFetchSize(maxResults);
        }
    }

    protected void prepareCriteria(Criteria criteria) {
        prepareCriteria(criteria, -1, -1);
    }
    
    protected void prepareCriteria(Criteria criteria, final int firstResult, final int maxResults) {
        if (isCacheQueries()) {
            criteria.setCacheable(true);
            if (getQueryCacheRegion() != null) {
                criteria.setCacheRegion(getQueryCacheRegion());
            }
        }
        applyTransactionTimeout(criteria, getSessionFactory());
        if (firstResult >= 0) {
            criteria.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
            criteria.setFetchSize(maxResults);
        }
    }
    
    @SuppressWarnings("rawtypes")
    protected void applyNamedParameterToQuery(Query queryObject, String paramName, Object value) throws HibernateException {
        if (value instanceof Collection) {
            queryObject.setParameterList(paramName, (Collection) value);
        } else if (value instanceof Object[]) {
            queryObject.setParameterList(paramName, (Object[]) value);
        } else {
            queryObject.setParameter(paramName, value);
        }
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void setAllowCreate(boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    public boolean isAllowCreate() {
        return this.allowCreate;
    }

    public void setCacheQueries(boolean cacheQueries) {
        this.cacheQueries = cacheQueries;
    }

    public boolean isCacheQueries() {
        return this.cacheQueries;
    }

    public void setQueryCacheRegion(String queryCacheRegion) {
        this.queryCacheRegion = queryCacheRegion;
    }

    public String getQueryCacheRegion() {
        return this.queryCacheRegion;
    }
    
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
