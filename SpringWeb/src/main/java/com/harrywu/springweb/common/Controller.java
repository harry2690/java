package com.harrywu.springweb.common;

import java.io.Serializable;

public interface Controller<Ent extends Entity, ID extends Serializable> {
    
    public GenericDAO<Ent, ID> getDao();
    
    public void setDao(GenericDAO<Ent, ID> dao);
    
    public Auditor getAuditor();
    
    public void setAuditor(Auditor auditor);
}
