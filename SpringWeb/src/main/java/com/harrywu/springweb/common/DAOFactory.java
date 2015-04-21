package com.harrywu.springweb.common;

import java.io.Serializable;

public interface DAOFactory <T extends Enum<T>> {

//    @SuppressWarnings("unchecked")
//    public GenericDAO getDAO(T daoId);
    public <EntityClass extends Entity, IdClass extends Serializable> 
                        GenericDAO<EntityClass, IdClass> getDAO(
                                                T daoId, 
                                                Class<EntityClass> clazz1,
                                                Class<IdClass> clazz2) throws Exception;
}
