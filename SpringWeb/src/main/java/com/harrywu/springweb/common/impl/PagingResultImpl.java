package com.harrywu.springweb.common.impl;

import com.harrywu.springweb.common.CustomResult;
import com.harrywu.springweb.common.PagingResult;
import com.harrywu.springweb.common.impl.CustomResultImpl;

public class PagingResultImpl<RESULT extends Object> extends CustomResultImpl<RESULT> implements PagingResult<RESULT> {
    
    private static final long serialVersionUID = -1381672251306417751L;
    
    private int firstResult = -1; 
    private int maxResults = -1; 
    private int totalResults = 0;
    
    public PagingResultImpl() {
        super();
    }
    
    public PagingResultImpl(boolean isSuccess, RESULT result, int firstResult, int maxResults, int totalResults) {
        super(isSuccess, result);
        this.firstResult = firstResult; 
        this.maxResults = maxResults; 
        this.totalResults = totalResults;
    }

    public PagingResultImpl(CustomResult<RESULT> result, int firstResult, int maxResults, int totalResults) {
        super(result.isSuccess(), result.getResult());
        this.firstResult = firstResult; 
        this.maxResults = maxResults; 
        this.totalResults = totalResults;
    }
    
    public static <T> CustomResult<T> result(boolean isSuccess, T result, int firstResult, int maxResults, int totalResults) {
        return new PagingResultImpl<T>(isSuccess, result, firstResult, maxResults, totalResults);
    }

    public static <T> CustomResult<T> result(CustomResult<T> result, int firstResult, int maxResults, int totalResults) {
        return new PagingResultImpl<T>(result, firstResult, maxResults, totalResults);
    }

    /* (non-Javadoc)
     * @see com.udn.common.PagingResult#getFirstResult()
     */
    @Override
    public int getFirstResult() {
        return firstResult;
    }

    /* (non-Javadoc)
     * @see com.udn.common.PagingResult#getMaxResults()
     */
    @Override
    public int getMaxResults() {
        return maxResults;
    }

    /* (non-Javadoc)
     * @see com.udn.common.PagingResult#getTotalResult()
     */
    @Override
    public int getTotalResults() {
        return totalResults;
    }

}
