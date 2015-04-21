package com.harrywu.springweb.common;


import com.harrywu.springweb.common.Entity;

public interface CustomResult<RESULT extends Object> extends Entity {
    
    boolean isSuccess();
    
    RESULT getResult();
    
    boolean hasValue();
}