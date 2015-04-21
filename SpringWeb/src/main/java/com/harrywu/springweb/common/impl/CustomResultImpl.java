package com.harrywu.springweb.common.impl;

import java.util.Collection;

import com.harrywu.springweb.common.CustomResult;
import com.harrywu.springweb.common.impl.EntityImpl;



public class CustomResultImpl<RESULT extends Object> extends EntityImpl implements CustomResult<RESULT>{
    
    private static final long serialVersionUID = 8493024738340645846L;
    
    private boolean isSuccess;
    private RESULT result;
    
    public CustomResultImpl() {
        isSuccess = false;
        result = null;
    }

    public CustomResultImpl(boolean isSuccess, RESULT result) {
        this.isSuccess = isSuccess;
        this.result = result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> CustomResult<T> result(boolean isSuccess, Object result) {
        return new CustomResultImpl<T>(isSuccess, (T) result);
    }
    
    @Override
    public boolean hasValue() {
        if (result instanceof Collection<?>)
            return isSuccess && ((result != null) && ((Collection<?>) result).size() > 0);
        return isSuccess && (result != null);
    }
    
    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }
    
    @Override
    public RESULT getResult() {
        return this.result;
    }
    
    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    
    public void setResult(RESULT result) {
        this.result = result;
    }

}
