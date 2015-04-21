package com.harrywu.springweb.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GenericDAO<Ent extends Entity, ID extends Serializable> {

    Class<Ent> getPersistentClass();

    CustomResult<Ent> insert(Ent entity) throws Exception;
    
    CustomResult<Ent> update(Ent entity) throws Exception;
    
    CustomResult<Object> delete(Ent entity) throws Exception;
    CustomResult<Object> delete(ID id) throws Exception;
    
    CustomResult<Ent> merge(Ent entity) throws Exception;
    CustomResult<Ent> saveOrUpdate(Ent entity) throws Exception;
    
    CustomResult<List<Ent>> list() throws Exception;
    CustomResult<List<Ent>> list(int firstResult, int maxResults) throws Exception;
    
    CustomResult<Ent> queryEntityById(ID id) throws Exception;

    CustomResult<List<Ent>> queryEntityBySql(String queryString, Map<String, Object> paramValues) throws Exception;

    CustomResult<List<Ent>> queryEntityBySql(String queryString, Map<String, Object> paramValues,
                                                    int firstResult, int maxResults) throws Exception;
    
    CustomResult<List<Ent>> find(String queryString) throws Exception;

    CustomResult<List<Ent>> find(String queryString, int firstResult, int maxResults) throws Exception;
    CustomResult<List<Ent>> find(String queryString, Object value) throws Exception;

    CustomResult<List<Ent>> find(String queryString, Object value, int firstResult, int maxResults) throws Exception;
    CustomResult<List<Ent>> find(String queryString, Object[] values) throws Exception;

    CustomResult<List<Ent>> find(String queryString, Object[] values, int firstResult, int maxResults) throws Exception;

    CustomResult<List<Ent>> findByNamedParam(String queryString, String paramName, Object value) throws Exception;

    CustomResult<List<Ent>> findByNamedParam(String queryString, String paramName, Object value,
                                             int firstResult, int maxResults) throws Exception;
    CustomResult<List<Ent>> findByNamedParam(String queryString, String[] paramNames, Object[] values) throws Exception;

    CustomResult<List<Ent>> findByNamedParam(String queryString, String[] paramNames, Object[] values,
                                             int firstResult, int maxResults) throws Exception;
    
    CustomResult<List<Ent>> findByValueBean(String queryString, Object valueBean) throws Exception;

    CustomResult<List<Ent>> findByValueBean(String queryString, Object valueBean,
                                            int firstResult, int maxResults) throws Exception;
    
    CustomResult<List<Ent>> findByExample(Ent entity) throws Exception;

    CustomResult<List<Ent>> findByExample(Ent entity, int firstResult, int maxResults) throws Exception;

    boolean isEntityExists(ID id) throws Exception;
    
    CustomResult<Object> uniqueResult(String queryString, String[] paramNames, Object[] values) throws Exception;
    CustomResult<Ent> uniqueResult(Ent entity) throws Exception;

    CustomResult<List<Ent>> findByNamedQuery(String queryName) throws Exception;

    CustomResult<List<Ent>> findByNamedQuery(String queryName, Object value) throws Exception;

    CustomResult<List<Ent>> findByNamedQuery(String queryName, Object[] values) throws Exception;

    CustomResult<List<Ent>> findByNamedQuery(String queryName, Object[] values,
                                             int firstResult, int maxResults) throws Exception;
    
    CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName,
                                                          String paramName, Object value) throws Exception;

    CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName,
                                                          String[] paramNames, Object[] values) throws Exception;

    CustomResult<List<Ent>> findByNamedQueryAndNamedParam(String queryName, String[] paramNames, Object[] values,
                                                          int firstResult, int maxResults) throws Exception;
    
    CustomResult<List<Ent>> findByNamedQueryAndValueBean(String queryName, Object valueBean) throws Exception;

    CustomResult<List<Ent>> findByNamedQueryAndValueBean(String queryName, Object valueBean,
                                                         int firstResult, int maxResults) throws Exception;

    String getNamedQueryString(String queryName);
    
    int executeUpdate(String queryString) throws Exception;
    @Deprecated
    int executeUpdate(String queryString, Object value) throws Exception;
    @Deprecated
    int executeUpdate(final String queryString, final Object[] values) throws Exception;
    int executeUpdate(String queryString, String paramName, Object value) throws Exception;
    int executeUpdate(final String queryString, final String[] paramNames, final Object[] values) throws Exception;

    int getFetchSize();
    void setFetchSize(int fetchSize);
    
    void flush();
    void clear();
}