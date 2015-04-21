package com.harrywu.springweb.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EntityField {
    
    public enum FIELD_TYPE {ALL, DB, TEMP, REQUIRED};
    
    FIELD_TYPE fieldType() default FIELD_TYPE.TEMP;
}
