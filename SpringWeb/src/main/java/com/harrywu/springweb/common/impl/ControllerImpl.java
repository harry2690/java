package com.harrywu.springweb.common.impl;

import java.io.Serializable;

import com.harrywu.springweb.common.Auditor;
import com.harrywu.springweb.common.Controller;
import com.harrywu.springweb.common.Entity;
import com.harrywu.springweb.common.GenericDAO;

public abstract class ControllerImpl<Ent extends Entity, ID extends Serializable> implements Controller<Ent, ID> {

    private GenericDAO<Ent, ID> dao;
    private Auditor auditor;
    
    @Override
    public GenericDAO<Ent, ID> getDao() {
        return this.dao;
    }
    
    @Override
    public void setDao(GenericDAO<Ent, ID> dao) {
        this.dao = dao;
    }

    @Override
    public Auditor getAuditor() {
        return this.auditor;
    }

    @Override
    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }
}
