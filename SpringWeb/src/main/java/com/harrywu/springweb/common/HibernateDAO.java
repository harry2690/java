package com.harrywu.springweb.common;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

public interface HibernateDAO<Ent extends Entity, ID extends Serializable> extends GenericDAO<Ent, ID> {
    
    CustomResult<List<Ent>> queryByCriteria(Serializable... criterion) throws Exception;
    CustomResult<List<Ent>> queryByCriteria(int firstResult, int maxResults, Serializable... criterion) throws Exception;

    CustomResult<List<Ent>> queryEntityByCriteria(Criterion... criterion) throws Exception;
    CustomResult<List<Ent>> queryEntityByCriteria(int firstResult, int maxResults, Criterion... criterion) throws Exception;
    
    Session openSession();
}

