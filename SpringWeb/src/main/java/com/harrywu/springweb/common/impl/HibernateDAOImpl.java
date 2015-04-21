package com.harrywu.springweb.common.impl;

import java.io.Serializable;

import com.harrywu.springweb.common.AbstractHibernateDAO;
import com.harrywu.springweb.common.Entity;


public class HibernateDAOImpl<Ent extends Entity, ID extends Serializable> extends AbstractHibernateDAO<Ent, ID> {
    
    public HibernateDAOImpl(Class<Ent> type) {
        super(type);
    }
}
