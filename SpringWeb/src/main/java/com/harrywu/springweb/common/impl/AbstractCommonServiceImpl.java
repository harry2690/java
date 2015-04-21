package com.harrywu.springweb.common.impl;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.harrywu.springweb.common.AuditedEntity;
import com.harrywu.springweb.common.CommonService;
import com.harrywu.springweb.common.CommonUtils;
import com.harrywu.springweb.common.CustomResult;
import com.harrywu.springweb.common.Entity;
import com.harrywu.springweb.common.Session;


public abstract class AbstractCommonServiceImpl<Ent extends Entity, ID extends Serializable> extends CommonControllerImpl<Ent, ID> implements CommonService<Ent, ID> {
    protected static final Log log = LogFactory.getLog(AbstractCommonServiceImpl.class);
            
    public AbstractCommonServiceImpl() {
    }

    @Override
    public CustomResult<List<Ent>> queryAll(int firstResult, int maxResults, Entity operator) throws Exception {
        CustomResult<List<Ent>> result = super.queryAll(firstResult, maxResults, operator);
        int totalResults = count(" from " + getPersistentClass().getSimpleName(), operator);
        return CustomPagingResultImpl.result(result, firstResult, maxResults, totalResults) ;
    }
    
    @Override
    public CustomResult<List<Ent>> query(Ent ent, int firstResult, int maxResults, Entity operator) throws Exception {
        Map<String, Object> values = ent.getValuedPersistentProperties();
        if (values.size() == 0)
            return queryAll(firstResult, maxResults, operator);

        int totalResults = count(ent, operator);
/* 因為有些 property 的 name 不是欄位名稱
        return CustomPagingResultImpl.result(super.query(genQuerySql(values, operator), values, firstResult, maxResults, operator),
                                             firstResult, maxResults, totalResults) ;
*/
        return CustomPagingResultImpl.result(super.query(ent, firstResult, maxResults, operator), firstResult, maxResults, totalResults) ;
    }
    
    @Override
    public CustomResult<Ent> insert(Ent data, Entity operator) throws Exception {
        if (data instanceof AuditedEntity) {
//            if (operator != null)
//                data.setProperty(Session.UPDATE_BY, operator.getProperty(Session.USER_UID));
//            data.setProperty(Session.UPDATE_DATE, new Date());
        }
        return super.insert(data, operator);
    }
    
    @Override
    public CustomResult<Ent> update(Ent data, Entity operator) throws Exception {
        if (data instanceof AuditedEntity) {
//            if (operator != null)
//                data.setProperty(Session.UPDATE_BY, operator.getProperty(Session.USER_UID));
//            data.setProperty(Session.UPDATE_DATE, new Date());
        }
        return super.update(data, operator);
    }
    
    @Override
    public int count(Ent data, Entity operator) throws Exception {
        Map<String, Object> values = data.getValuedPersistentProperties();
        String sql = genQuerySql(values, operator);
        sql = "select count(*) " + sql;
        CustomResult<?> result = uniqueResult(sql, values, operator);
        if (result.hasValue())
            return ((Number) result.getResult()).intValue();
        return 0;
    }

    @Override
    public CustomResult<Integer> count(Entity operator) throws Exception {
        return CustomResultImpl.result(true, super.count(getPersistentClass().newInstance(), operator));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<Number> sum(final String columnName, Ent data, Entity operator) throws Exception {
        Map<String, Object> values = data.getValuedPersistentProperties();
        String sql = genQuerySql(values, operator);
        sql = "select sum(" + columnName + ") " + sql;
        CustomResult<?> result = uniqueResult(sql, values, operator);
        if (result.hasValue())
            return (CustomResult<Number>) result;
        return CustomResultImpl.result(true, 0);
    }
    
    @Override
    public CustomResult<Number> sum(final String columnName, final String queryString, Entity operator) throws Exception {
        return sum(columnName, queryString, (String) null, null, operator);
    }
    
    @Override
    public CustomResult<Number> sum(final String columnName, final String queryString, final String paramName, final Object value, final Entity operator) throws Exception {
        return sum(columnName, queryString, new String[] {paramName}, new Object[] {value}, operator);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public CustomResult<Number> sum(final String columnName, final String queryString, final String[] paramNames, final Object[] values, final Entity operator) throws Exception {
        CustomResult<?> result = uniqueResult("select sum(" + columnName + ") " + queryString, paramNames, values, operator);
        if (result.hasValue())
            return (CustomResult<Number>) result;
        return CustomResultImpl.result(true, 0);
    }

    /**
     * 呼叫 query(Ent) 時會使用
     */
    @Override
    protected String doGenQuerySql(Map<String, Object> values, String otherCondition, Entity operator) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" from " + getPersistentClass().getSimpleName() + " where 1=1");
        
        if (values.size() == 0) {
            if (otherCondition != null && otherCondition.length() != 0) {
                sql.append(" and ");
                sql.append(otherCondition);
            }
            return sql.toString();
        }

        String s = doGenEntityWhereSql(values, null);
        sql.append(" " + s);
        
        if (otherCondition != null && otherCondition.length() != 0) {
            sql.append(" and ");
            sql.append(otherCondition);
        }
        
        return sql.toString();
    }

    protected String doGenEntityWhereSql(Map<String, Object> values, String alias) throws Exception {
        StringBuilder sql = new StringBuilder();

        if (values.size() == 0)
            return sql.toString();
        
        Map<String, String> likeFields = new HashMap<String, String>();

        Set<String> fieldNames = values.keySet();
        for (String fieldName : fieldNames) {
            Object str = values.get(fieldName);
            if (str instanceof String) {
                String value = (String) str;
                if (value.indexOf("*") >= 0 || value.indexOf("%") >= 0) {
                    String likeFieldQueryString = CommonUtils.genLikeFieldQueryString(alias, fieldName, values, "*");
                    if (likeFieldQueryString != null) {
                        sql.append(" and " + likeFieldQueryString);
                        likeFields.put(fieldName, fieldName);
                    }
                }
            } else {
/*
                // Entity
                if (str instanceof Entity) {
                    String s = " " + doGenEntityWhereSql((Entity) str, alias);
                    sql.append(s);
                }
*/
            }
            if (!likeFields.containsKey(fieldName) && !(str instanceof Entity)) {
                if (alias != null && alias.trim().length() > 0)
                    sql.append(" and " + alias + "." + fieldName + " = :" + fieldName);
                else
                    sql.append(" and " + fieldName + " = :" + fieldName);
            }
        }
        return sql.toString();
    }
}

