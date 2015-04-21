package com.harrywu.springweb.common;

public interface PagingResult<RESULT extends Object> extends CustomResult<RESULT> {

    int getFirstResult();
    int getMaxResults();
    int getTotalResults();
}
