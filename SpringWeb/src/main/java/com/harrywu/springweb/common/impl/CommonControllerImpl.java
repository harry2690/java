package com.harrywu.springweb.common.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.harrywu.springweb.common.CommonController;
import com.harrywu.springweb.common.CustomResult;
import com.harrywu.springweb.common.Entity;

public class CommonControllerImpl<Ent extends Entity, ID extends Serializable> extends ControllerImpl<Ent, ID> implements CommonController<Ent, ID> {

    @Override
    public Class<Ent> getPersistentClass() {
        return getDao().getPersistentClass();
    }

    @Override
    public CustomResult<Ent> insert(Ent data, Entity operator) throws Exception {
        return getDao().insert(data);
    }

    @Override
    public CustomResult<Ent> update(Ent data, Entity operator) throws Exception {
        return getDao().update(data);
    }
    
    @Override
    public CustomResult<Object> delete(Ent data, Entity operator) throws Exception {
        return getDao().delete(data);
    }
    
    @Override
    public CustomResult<Object> delete(ID id, Entity operator) throws Exception {
        return getDao().delete(id);
    }

    @Override
    public CustomResult<Ent> merge(Ent data, Entity operator) throws Exception {
        return getDao().merge(data);
    }
    
    @Override
    public CustomResult<Ent> saveOrUpdate(Ent data, Entity operator) throws Exception {
        return getDao().saveOrUpdate(data);
    }
    
    @Override
    public void flush() {
        getDao().flush();
    }

    @Override
    public CustomResult<List<Ent>> queryAll(Entity operator) throws Exception {
        return queryAll(-1, -1, operator);
    }

    @Override
    public CustomResult<List<Ent>> queryAll(int firstResult, int maxResults, Entity operator) throws Exception {
        return getDao().list(firstResult, maxResults);
    }

    @Override
    public CustomResult<List<Ent>> query(Ent data, Entity operator) throws Exception {
        return query(data, -1, -1, operator);
    }
    
    @Override
    public CustomResult<List<Ent>> query(Ent data, int firstResult, int maxResults, Entity operator) throws Exception {
        return getDao().findByExample(data, firstResult, maxResults);
    }
    
    @Override
    public CustomResult<List<Ent>> queryBySql(String queryString, Entity operator) throws Exception {
        return queryBySql(queryString, -1, -1, operator);
    }
    
    @Override
    public CustomResult<List<Ent>> queryBySql(String queryString, int firstResult, int maxResults, Entity operator) throws Exception {
        return getDao().find(queryString, firstResult, maxResults);
    }
    
    @Override
    public CustomResult<List<Ent>> query(String queryString, Map<String, Object> paramValues, Entity operator) throws Exception {
        return query(queryString, paramValues, -1, -1, operator);
    }

    @Override
    public CustomResult<List<Ent>> query(String queryString, Map<String, Object> paramValues, int firstResult, int maxResults, Entity operator) throws Exception {
        return getDao().queryEntityBySql(queryString, paramValues, firstResult, maxResults);
    }
    
    @Deprecated
    @Override
    public CustomResult<List<Ent>> query(String queryString, Object[] values, Entity operator) throws Exception {
        return query(queryString, values, -1, -1, operator);
    }

    @Deprecated
    @Override
    public CustomResult<List<Ent>> query(String queryString, Object[] values, int firstResult, int maxResults, Entity operator) throws Exception {
        return getDao().find(queryString, values, firstResult, maxResults);
    }
            
    @Override
    public CustomResult<List<Ent>> query(String queryString, String paramName, Object value, Entity operator) throws Exception {
        return query(queryString, paramName, value, -1, -1, operator);
    }

    @Override
    public CustomResult<List<Ent>> query(String queryString, String paramName, Object value, int firstResult, int maxResults, Entity operator) throws Exception {
        return query(queryString, new String[] {paramName}, new Object[] {value}, firstResult, maxResults, operator);
    }

    @Override
    public CustomResult<List<Ent>> query(String queryString, String[] paramNames, Object[] values, Entity operator) throws Exception {
        return query(queryString, paramNames, values, -1, -1, operator);
    }

    @Override
    public CustomResult<List<Ent>> query(String queryString, String[] paramNames, Object[] values, int firstResult, int maxResults, Entity operator) throws Exception {
        return getDao().findByNamedParam(queryString, paramNames, values, firstResult, maxResults);
    }
            
    @Override
    public CustomResult<List<Ent>> query(String queryString, Entity valueBean, Entity operator) throws Exception {
        return query(queryString, valueBean, -1, -1, operator);
    }

    @Override
    public CustomResult<List<Ent>> query(String queryString, Entity valueBean, int firstResult, int maxResults, Entity operator) throws Exception {
        return getDao().findByValueBean(queryString, valueBean, firstResult, maxResults);
    }
            
    @Override
    public CustomResult<Ent> query(ID id, Entity operator) throws Exception {
        return getDao().queryEntityById(id);
    }

    @Override
    public CustomResult<Ent> uniqueResult(Ent data, Entity operator) throws Exception {
        CustomResult<List<Ent>> result = query(data, 0, 1, operator);
        return returnUnique(result);
    }

    @Override
    public CustomResult<Ent> uniqueResult(String queryString, Map<String, Object> paramValues, Entity operator) throws Exception {
        CustomResult<List<Ent>> result = query(queryString, paramValues, 0, 1, operator);
        return returnUnique(result);
    }

    @Override
    public CustomResult<Ent> uniqueResult(String queryString, Object[] values, Entity operator) throws Exception {
        CustomResult<List<Ent>> result = query(queryString, values, 0, 1, operator);
        return returnUnique(result);
    }

    @Override
    public CustomResult<Ent> uniqueResult(String queryString, String paramName, Object value, Entity operator) throws Exception {
        return uniqueResult(queryString, new String[] {paramName}, new Object[] {value}, operator);
    }
    
    @Override
    public CustomResult<Ent> uniqueResult(String queryString, String[] paramNames, Object[] values, Entity operator) throws Exception {
        CustomResult<List<Ent>> result = getDao().findByNamedParam(queryString, paramNames, values, 0, 1);
        return returnUnique(result);
    }

    @Override
    public CustomResult<Ent> uniqueResult(String queryString, Entity valueBean, Entity operator) throws Exception {
        CustomResult<List<Ent>> result = getDao().findByValueBean(queryString, valueBean, 0, 1);
        return returnUnique(result);
    }

    @Override
    public CustomResult<Ent> uniqueResult(String queryString, Entity operator) throws Exception {
        CustomResult<List<Ent>> result = getDao().find(queryString, 0, 1);
        return returnUnique(result);
    }

    @Override
    public CustomResult<Object> uniqueResult(String queryString, String[] paramNames, Object[] values) throws Exception {
        return getDao().uniqueResult(queryString, paramNames, values);
    }
    
    @Override
    public int executeUpdate(String queryString, Entity operator) throws Exception {
        return getDao().executeUpdate(queryString);
    }
    
    @Deprecated
    @Override
    public int executeUpdate(String queryString, Object value, Entity operator) throws Exception {
        return executeUpdate(queryString, new Object[] {value}, operator);
    }
    
    @Deprecated
    @Override
    public int executeUpdate(final String queryString, final Object[] values, Entity operator) throws Exception {
        return getDao().executeUpdate(queryString, values);
    }
    
    @Override
    public int executeUpdate(String queryString, String paramName, Object value, Entity operator) throws Exception {
        return getDao().executeUpdate(queryString, paramName, value);
    }

    @Override
    public int executeUpdate(String queryString, String[] paramNames, Object[] values, Entity operator) throws Exception {
        return getDao().executeUpdate(queryString, paramNames, values);
    }
    
    @Override
    public CustomResult<List<Ent>> findByNamedQuery(String queryName) throws Exception {
        return getDao().findByNamedQuery(queryName);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQuery(String queryName, Object value) throws Exception {
        return getDao().find(queryName, value);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQuery(String queryName, Object[] values) throws Exception {
        return getDao().findByNamedQuery(queryName, values);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName,
                                                                 String paramName, Object value) throws Exception {
        return getDao().findByNamedQueryAndNamedParam(queryName, paramName, value);        
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName,
                                                                 String[] paramNames, Object[] values) throws Exception {
        return getDao().findByNamedQueryAndNamedParam(queryName, paramNames, values);
    }

    @Override
    public CustomResult<List<Ent>> findByNamedQueryAndValueBean(String queryName, Entity valueBean) throws Exception {
        return getDao().findByNamedQueryAndValueBean(queryName, valueBean);
    }
    
    @Override
    public String getNamedQueryString(String queryName) {
        return getDao().getNamedQueryString(queryName);
    }
    
    @Override
    public String genQuerySql(Map<String, Object> values, Entity operator) throws Exception {
        return doGenQuerySql(values, null, operator);
    }

    @Override
    public String genQuerySql(Map<String, Object> values, String otherCondition, Entity operator) throws Exception {
        return doGenQuerySql(values, otherCondition, operator);
    }

    @Override
    public int count(Ent data, Entity operator) throws Exception {
        Map<String, Object> values = data.getValuedProperties();
        String sql = genQuerySql(values, operator);
        sql = "select count(*) " + sql;
        CustomResult<?> result = uniqueResult(sql, values, operator);
        if (result.hasValue())
            return ((Number) result.getResult()).intValue();
        return 0;
    }

    @Override
    public int count(String queryString, Entity operator) throws Exception {
        CustomResult<?> result = uniqueResult("select count(*) " + queryString, operator);
        if (result.hasValue())
            return ((Number) result.getResult()).intValue();
        return 0;
    }

    @Override
    public int count(String queryString, String paramName, Object value, Entity operator) throws Exception {
        CustomResult<?> result = uniqueResult("select count(*) " + queryString, paramName, value, operator);
        if (result.hasValue())
            return ((Number) result.getResult()).intValue();
        return 0;
    }

    @Override
    public int count(String queryString, String[] paramNames, Object[] values, Entity operator) throws Exception {
        CustomResult<?> result = uniqueResult("select count(*) " + queryString, paramNames, values, operator);
        if (result.hasValue())
            return ((Number) result.getResult()).intValue();
        return 0;
    }
    
    @Override
    public Object max(final String columnName, Ent data, Entity operator) throws Exception {
        Map<String, Object> values = data.getValuedProperties();
        String sql = genQuerySql(values, operator);
        sql = "select max(" + columnName + ") " + sql;
        CustomResult<?> result = uniqueResult(sql, values, operator);
        if (result.hasValue())
            return result.getResult();
        return null;
    }
    
    @Override
    public Object max(final String columnName, final String queryString, Entity operator) throws Exception {
        CustomResult<?> result = uniqueResult("select max(" + columnName + ") " + queryString, operator);
        if (result.hasValue())
            return result.getResult();
        return null;
    }
    
    @Override
    public Object max(final String columnName, final String queryString, final String paramName, final Object value, final Entity operator) throws Exception {
        CustomResult<?> result = uniqueResult("select max(" + columnName + ") " + queryString, paramName, value, operator);
        if (result.hasValue())
            return result.getResult();
        return null;
    }
    
    @Override
    public Object max(final String columnName, final String queryString, final String[] paramNames, final Object[] values, final Entity operator) throws Exception {
        CustomResult<?> result = uniqueResult("select max(" + columnName + ") " + queryString, paramNames, values, operator);
        if (result.hasValue())
            return result.getResult();
        return null;
    }
    
    protected String doGenQuerySql(Map<String, Object> values, String otherCondition, Entity operator) throws Exception {
        throw new Exception("[CommonControllerImpl.doGenQuerySql] not implemented");
    }
    
    protected <T> CustomResult<T> returnUnique(CustomResult<List<T>> result) {
        if (result.hasValue())
            return new CustomResultImpl<T>(true, result.getResult().get(0));
        return new CustomResultImpl<T>(false, null);
    }

}
