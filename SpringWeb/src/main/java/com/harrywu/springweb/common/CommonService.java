package com.harrywu.springweb.common;

import java.io.Serializable;

public interface CommonService<Ent extends Entity, ID extends Serializable> extends CommonController<Ent, ID> {
    CustomResult<Integer> count(Entity operator) throws Exception;
    
    CustomResult<Number> sum(final String columnName, Ent data, Entity operator) throws Exception; 
    CustomResult<Number> sum(final String columnName, final String queryString, Entity operator) throws Exception;
    CustomResult<Number> sum(final String columnName, final String queryString, final String paramName, final Object value, final Entity operator) throws Exception;
    CustomResult<Number> sum(final String columnName, final String queryString, final String[] paramNames, final Object[] values, final Entity operator) throws Exception;

}
