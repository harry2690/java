package com.harrywu.springweb.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface CommonController<Ent extends Entity, ID extends Serializable> {

    Class<Ent> getPersistentClass();

    CustomResult<Ent> insert(Ent data, Entity operator) throws Exception;
    
    CustomResult<Ent> update(Ent data, Entity operator) throws Exception;
    
    CustomResult<Object> delete(Ent data, Entity operator) throws Exception;
    CustomResult<Object> delete(ID id, Entity operator) throws Exception;
    
    CustomResult<Ent> merge(Ent data, Entity operator) throws Exception;
    
    CustomResult<Ent> saveOrUpdate(Ent data, Entity operator) throws Exception;
    
    void flush();
    
    CustomResult<List<Ent>> queryAll(Entity operator) throws Exception;
    CustomResult<List<Ent>> queryAll(int firstResult, int maxResults, Entity operator) throws Exception;
    
    CustomResult<List<Ent>> query(Ent data, Entity operator) throws Exception;
    CustomResult<List<Ent>> query(Ent data, int firstResult, int maxResults, Entity operator) throws Exception;
    
    CustomResult<List<Ent>> queryBySql(String queryString, Entity operator) throws Exception;
    CustomResult<List<Ent>> queryBySql(String queryString, int firstResult, int maxResults, Entity operator) throws Exception;
    
    CustomResult<List<Ent>> query(String queryString, Map<String, Object> paramValues, Entity operator) throws Exception;
    CustomResult<List<Ent>> query(String queryString, Map<String, Object> paramValues, int firstResult, int maxResults, Entity operator) throws Exception;
    
    @Deprecated
    CustomResult<List<Ent>> query(String queryString, Object[] values, Entity operator) throws Exception;
    @Deprecated
    CustomResult<List<Ent>> query(String queryString, Object[] values, int firstResult, int maxResults, Entity operator) throws Exception;
    
    CustomResult<List<Ent>> query(String queryString, String paramName, Object value, Entity operator) throws Exception;
    CustomResult<List<Ent>> query(String queryString, String paramName, Object value, int firstResult, int maxResults, Entity operator) throws Exception;
    CustomResult<List<Ent>> query(String queryString, String[] paramNames, Object[] values, Entity operator) throws Exception;
    CustomResult<List<Ent>> query(String queryString, String[] paramNames, Object[] values, int firstResult, int maxResults, Entity operator) throws Exception;
    
    CustomResult<List<Ent>> query(String queryString, Entity valueBean, Entity operator) throws Exception;
    CustomResult<List<Ent>> query(String queryString, Entity valueBean, int firstResult, int maxResults, Entity operator) throws Exception;

    CustomResult<Ent> query(ID id, Entity operator) throws Exception;

    CustomResult<Ent> uniqueResult(Ent data, Entity operator) throws Exception;
    CustomResult<Ent> uniqueResult(String queryString, Map<String, Object> paramValues, Entity operator) throws Exception;
    @Deprecated
    CustomResult<Ent> uniqueResult(String queryString, Object[] values, Entity operator) throws Exception;
    CustomResult<Ent> uniqueResult(String queryString, String paramName, Object value, Entity operator) throws Exception;
    CustomResult<Ent> uniqueResult(String queryString, String[] paramNames, Object[] values, Entity operator) throws Exception;
    CustomResult<Ent> uniqueResult(String queryString, Entity valueBean, Entity operator) throws Exception;
    CustomResult<Ent> uniqueResult(String queryString, Entity operator) throws Exception;
    @Deprecated
    CustomResult<Object> uniqueResult(String queryString, String[] paramNames, Object[] values) throws Exception;
    
    int executeUpdate(final String queryString, final Entity operator) throws Exception;
    @Deprecated
    int executeUpdate(String queryString, Object value, Entity operator) throws Exception;
    @Deprecated
    int executeUpdate(final String queryString, final Object[] values, Entity operator) throws Exception;

    int executeUpdate(final String queryString, final String paramName, final Object value, final Entity operator) throws Exception;
    int executeUpdate(final String queryString, final String[] paramNames, final Object[] values, final Entity operator) throws Exception;

    CustomResult<List<Ent>> findByNamedQuery(String queryName) throws Exception;
    CustomResult<List<Ent>> findByNamedQuery(String queryName, Object value) throws Exception;
    CustomResult<List<Ent>> findByNamedQuery(String queryName, Object[] values) throws Exception;

    CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName, String paramName, Object value) throws Exception;
    CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName, String[] paramNames, Object[] values) throws Exception;

    CustomResult<List<Ent>> findByNamedQueryAndValueBean(String queryName, Entity valueBean) throws Exception;

    String getNamedQueryString(String queryName);
    
    String genQuerySql(Map<String, Object> values, Entity operator) throws Exception;
    String genQuerySql(Map<String, Object> values, String otherCondition, Entity operator) throws Exception;

    /**
     * 銝���select count(*)
     * @param data
     * @param operator
     * @return
     * @throws Exception
     */
    int count(Ent data, Entity operator) throws Exception;
    
    /**
     * 銝���select count(*)
     * @param queryString
     * @param operator
     * @return
     * @throws Exception
     */
    int count(final String queryString, Entity operator) throws Exception;
    /**
     * 銝���select count(*)
     * @param queryString
     * @param paramName
     * @param value
     * @param operator
     * @return
     * @throws Exception
     */
    int count(final String queryString, final String paramName, final Object value, final Entity operator) throws Exception;
    /**
     * 銝���select count(*)
     * @param queryString
     * @param paramNames
     * @param values
     * @param operator
     * @return
     * @throws Exception
     */
    int count(final String queryString, final String[] paramNames, final Object[] values, final Entity operator) throws Exception;

    /**
     * 銝���select max() --> 蝔��芸�蝯� select max(columnName)
     * @param columnName
     * @param data
     * @param operator
     * @return
     * @throws Exception
     */
    Object max(final String columnName, Ent data, Entity operator) throws Exception; 
    Object max(final String columnName, final String queryString, Entity operator) throws Exception;
    Object max(final String columnName, final String queryString, final String paramName, final Object value, final Entity operator) throws Exception;
    Object max(final String columnName, final String queryString, final String[] paramNames, final Object[] values, final Entity operator) throws Exception;
}
