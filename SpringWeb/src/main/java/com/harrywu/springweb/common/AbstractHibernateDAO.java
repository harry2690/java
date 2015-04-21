package com.harrywu.springweb.common;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import com.harrywu.springweb.common.impl.CustomResultImpl;


public abstract class AbstractHibernateDAO<Ent extends Entity, ID extends Serializable> implements HibernateDAO<Ent, ID> {
    private Class<Ent> persistentClass;
    
    private int fetchSize = 0;
    private CustomHibernateTemplate hibernateTemplate;

    public AbstractHibernateDAO(Class<Ent> type) {
        persistentClass = type;
    }    
    
    @SuppressWarnings("unchecked")
    public AbstractHibernateDAO() {
        this.persistentClass = (Class<Ent>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<Ent> getPersistentClass() {
        return persistentClass;
    }

    public final void setSessionFactory(SessionFactory sessionFactory) {
        if (this.hibernateTemplate == null || sessionFactory != this.hibernateTemplate.getSessionFactory()) {
            this.hibernateTemplate = createHibernateTemplate(sessionFactory);
        }
    }

    protected CustomHibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
        hibernateTemplate = new CustomHibernateTemplate(sessionFactory);
        hibernateTemplate.setCacheQueries(true);
        return hibernateTemplate;
    }
    
    @Override
    public CustomResult<Ent> insert(Ent entity) throws Exception {
        getCustomHibernateTemplate().save(entity);
        return new CustomResultImpl<Ent>(true, entity);
    }

    @Override
    public CustomResult<Ent> update(Ent entity) throws Exception {
        getCustomHibernateTemplate().update(getCustomHibernateTemplate().merge(entity));
        return new CustomResultImpl<Ent>(true, entity);
    }

    @Override
    public CustomResult<Object> delete(Ent entity) throws Exception {
//        getDCHibernateTemplate().delete(getDCHibernateTemplate().merge(entity));
        getCustomHibernateTemplate().delete(entity);
        // Fixed for delete and then insert same row, duplicate violation
//        getDCHibernateTemplate().flush();
        return new CustomResultImpl<Object>(true, null);
    }

    @Override
    public CustomResult<Object> delete(ID id) throws Exception {
        getCustomHibernateTemplate().delete(getCustomHibernateTemplate().get(getPersistentClass(), id));
        // Fixed for delete and then insert same row, duplicate violation
//        getDCHibernateTemplate().flush();
        return new CustomResultImpl<Object>(true, null);
    }
    
    @Override
    public CustomResult<Ent> merge(Ent entity) throws Exception {
        entity = getCustomHibernateTemplate().merge(entity);
        return new CustomResultImpl<Ent>(true, entity);
    }
    
    @Override
    public CustomResult<Ent> saveOrUpdate(Ent entity) throws Exception {
        getCustomHibernateTemplate().saveOrUpdate(entity);
        return new CustomResultImpl<Ent>(true, entity);
    }

    @Override
    public CustomResult<List<Ent>> list() throws Exception {
        return queryEntityByCriteria();
    }
    
    @Override
    public CustomResult<List<Ent>> list(int firstResult, int maxResults) throws Exception {
        return queryEntityByCriteria(firstResult, maxResults);
    }
    
    @Override
    public CustomResult<Ent> queryEntityById(ID id) throws Exception {
        Ent entity = getCustomHibernateTemplate().get(getPersistentClass(), id);
        return new CustomResultImpl<Ent>(true, entity);
    }

    @Override
    public CustomResult<List<Ent>> queryEntityByCriteria(Criterion... criterion) throws Exception {
        return queryEntityByCriteria(-1, -1, criterion);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<List<Ent>> queryEntityByCriteria(int firstResult, int maxResults, Criterion... criterion) throws Exception {
        DetachedCriteria detchedCriteria = DetachedCriteria.forClass(getPersistentClass());
        for (Criterion c: criterion) {
            detchedCriteria.add(c);
        }
        List<Ent> list = getCustomHibernateTemplate().findByCriteria(detchedCriteria, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }

    @Override
    public CustomResult<List<Ent>> queryEntityBySql(String queryString, Map<String, Object> paramValues) throws Exception {
        return queryEntityBySql(queryString, paramValues, -1, -1);
    }

    @Override
    public CustomResult<List<Ent>> queryEntityBySql(String queryString, Map<String, Object> paramValues,
                                                    int firstResult, int maxResults) throws Exception {
        if (paramValues != null && paramValues.size() > 0) {
            String[] paramNames = paramValues.keySet().toArray(new String[paramValues.size()]);
            Object[] values = paramValues.values().toArray();
            return findByNamedParam(queryString, paramNames, values, firstResult, maxResults);
        }
        return find(queryString, firstResult, maxResults);
    }
    
    @Override
    public CustomResult<List<Ent>> find(String queryString) throws Exception {
        return find(queryString, -1, -1);
    }

    @Override
    public CustomResult<List<Ent>> find(String queryString, int firstResult, int maxResults) throws Exception {
        return find(queryString, (Object[]) null, firstResult, maxResults);
    }

    @Override
    public CustomResult<List<Ent>> find(String queryString, Object value) throws Exception {
        return find(queryString, value, -1, -1);
    }

    @Override
    public CustomResult<List<Ent>> find(String queryString, Object value, int firstResult, int maxResults) throws Exception {
        return find(queryString, new Object[] {value}, firstResult, maxResults);
    }

    @Override
    public CustomResult<List<Ent>> find(String queryString, Object[] values) throws Exception {
        return find(queryString, values, -1, -1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<List<Ent>> find(String queryString, Object[] values, int firstResult, int maxResults) throws Exception {
        List<Ent> list = getCustomHibernateTemplate().find(queryString, values, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedParam(String queryString, String paramName, Object value) throws Exception {
        return findByNamedParam(queryString, new String[] {paramName}, new Object[] {value});
    }
    
    @Override
    public CustomResult<List<Ent>> findByNamedParam(String queryString, String paramName, Object value,
                                                    int firstResult, int maxResults) throws Exception {
        return findByNamedParam(queryString, new String[] {paramName}, new Object[] {value}, -1, -1);
    }
    
    @Override
    public CustomResult<List<Ent>> findByNamedParam(String queryString, String[] paramNames, Object[] values) throws Exception {
        return findByNamedParam(queryString, paramNames, values, -1, -1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<List<Ent>> findByNamedParam(String queryString, String[] paramNames, Object[] values, int firstResult, int maxResults) throws Exception {
        List<Ent> list = getCustomHibernateTemplate().findByNamedParam(queryString, paramNames, values, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }

    @Override
    public CustomResult<List<Ent>> findByValueBean(String queryString, Object valueBean) throws Exception {
        return findByValueBean(queryString, valueBean, 0 , 0);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<List<Ent>> findByValueBean(String queryString, Object valueBean,
                                                   int firstResult, int maxResults) throws Exception {
        List<Ent> list = getCustomHibernateTemplate().findByValueBean(queryString, valueBean, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }
    
    @Override
    public CustomResult<List<Ent>> findByExample(Ent entity) throws Exception {
        return findByExample(entity, -1, -1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<List<Ent>> findByExample(Ent entity, int firstResult, int maxResults) throws Exception {
        List<Ent> list = getCustomHibernateTemplate().findByExample(entity, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }
    
    @Override
    public boolean isEntityExists(ID id) throws Exception {
        CustomResult<Ent> result = queryEntityById(id); 
        return result.hasValue();
    }
   
    @Override
    public CustomResult<Object> uniqueResult(String queryString, String[] paramNames, Object[] values) throws Exception {
        CustomResult<List<Ent>> result = findByNamedParam(queryString, paramNames, values, 0, 1);
        if (result.hasValue())
            return new CustomResultImpl<Object>(true, result.getResult().get(0));
        return new CustomResultImpl<Object>(false, null);
    }

    @Override
    public CustomResult<Ent> uniqueResult(Ent entity) throws Exception {
        CustomResult<List<Ent>> result = findByExample(entity, 0, 1);
        if (result.hasValue())
            return new CustomResultImpl<Ent>(true, result.getResult().get(0));
        return new CustomResultImpl<Ent>(false, null);
    }

    @Override
    public CustomResult<List<Ent>> queryByCriteria(Serializable... criterion) throws Exception {
        return queryByCriteria(-1, -1, criterion);
    }

    @Override
    public CustomResult<List<Ent>> queryByCriteria(int firstResult, int maxResults, Serializable... criterion) throws Exception {
        DetachedCriteria detchedCriteria = DetachedCriteria.forClass(getPersistentClass());
        for (Serializable c: criterion) {
            if (c instanceof Criterion)
                detchedCriteria.add((Criterion) c);
            else
                if (c instanceof Order)
                    detchedCriteria.addOrder((Order) c);
        }
        @SuppressWarnings("unchecked")
        List<Ent> list = getCustomHibernateTemplate().findByCriteria(detchedCriteria, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQuery(String queryName) throws Exception {
        return findByNamedQuery(queryName, (Object[]) null);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQuery(String queryName, Object value) throws Exception {
        return findByNamedQuery(queryName, new Object[] {value});
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQuery(String queryName, Object[] values) throws Exception {
        return findByNamedQuery(queryName, values, -1, -1);
    }

    @SuppressWarnings("unchecked")
    public CustomResult<List<Ent>> findByNamedQuery(String queryName, Object[] values,
                                                    int firstResult, int maxResults) throws Exception {
        List<Ent> list = getCustomHibernateTemplate().findByNamedQuery(queryName, values, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }
    
    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName,
                                                                 String paramName, Object value) throws Exception {
        return findByNamedQueryAndNamedParam(queryName, new String[] {paramName}, new Object[] {value});
    }
    
    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName, String[] paramNames, Object[] values) throws Exception {
        return findByNamedQueryAndNamedParam(queryName, paramNames, values, -1, -1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName, String[] paramNames, Object[] values,
                                                                 int firstResult, int maxResults) throws Exception {
        List<Ent> list = getCustomHibernateTemplate().findByNamedQueryAndNamedParam(queryName, paramNames, values, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndValueBean(String queryName, Object valueBean) throws Exception {
        return findByNamedQueryAndValueBean(queryName, valueBean, -1, -1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndValueBean(String queryName, Object valueBean,
                                                                int firstResult, int maxResults) throws Exception {
        List<Ent> list = getCustomHibernateTemplate().findByNamedQueryAndValueBean(queryName, valueBean, firstResult, maxResults);
        return new CustomResultImpl<List<Ent>>(true, list);
    }
    
    @Override
    public String getNamedQueryString(String queryName) {
        return getCustomHibernateTemplate().getSession().getNamedQuery(queryName).getQueryString();
    }

    @Override
    public int executeUpdate(String queryString) throws Exception {
        return executeUpdate(queryString, null);
    }

    @Override
    public int executeUpdate(String queryString, Object value) throws Exception {
        return executeUpdate(queryString, new Object[] {value});
    }

    @Override
    public int executeUpdate(final String queryString, final Object[] values) throws Exception {
        return getCustomHibernateTemplate().bulkUpdate(queryString, values);
    }

    @Override
    public int executeUpdate(String queryString, String paramName, Object value) throws Exception {
        return executeUpdate(queryString, new String[] {paramName}, new Object[] {value});
    }

    @Override
    public int executeUpdate(String queryString, String[] paramNames, Object[] values) throws Exception {
        return getCustomHibernateTemplate().bulkUpdate(queryString, paramNames, values);
    }
    
    @Override
    public int getFetchSize() {
        return this.fetchSize;
    }

    @Override
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        getCustomHibernateTemplate().setFetchSize(this.fetchSize);
    }

    @Override
    public void flush() {
        getCustomHibernateTemplate().flush();
    }
    
    @Override
    public void clear() {
        getCustomHibernateTemplate().clear();
    }

    @Override
    public Session openSession() {
        return getCustomHibernateTemplate().getSession();
    }

    public CustomHibernateTemplate getCustomHibernateTemplate() {
        return hibernateTemplate;
    }
    
}

