package com.harrywu.springweb.common.impl;

import javax.persistence.MappedSuperclass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.harrywu.springweb.common.CustomEntity;

@MappedSuperclass
public class CustomEntityImpl extends EntityImpl implements CustomEntity {
    protected static final Log log = LogFactory.getLog(CustomEntityImpl.class);
    
    private static final long serialVersionUID = 3530105343479474610L;

    public CustomEntityImpl() {
    }

}
