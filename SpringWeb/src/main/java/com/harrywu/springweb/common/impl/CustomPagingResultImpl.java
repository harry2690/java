package com.harrywu.springweb.common.impl;

import com.harrywu.springweb.common.CustomResult;


public class CustomPagingResultImpl<RESULT extends Object> extends PagingResultImpl<RESULT> {
    private static final long serialVersionUID = -5265198165363878307L;
    
    private int totalPages = 0;
    private int currentPage = 0;
    private int surplusResults = 0;

    public CustomPagingResultImpl(CustomResult<RESULT> result, int currentPage, int firstResult, int surplusResults, int maxResults, int totalResults) {
        super(result, firstResult, maxResults, totalResults);
        this.totalPages = getTotalPages(maxResults, totalResults);
        this.currentPage = currentPage;
        this.surplusResults = surplusResults;
    }

    public static <T> CustomResult<T> result(CustomResult<T> result, int firstResult, int maxResults, int totalResults) {
        return new CustomPagingResultImpl<T>(result, -1, firstResult, 0, maxResults, totalResults);
    }

    public static <T> CustomResult<T> result(CustomResult<T> result, int currentPage, int firstResult, int surplusResults, int maxResults, int totalResults) {
        return new CustomPagingResultImpl<T>(result, currentPage, firstResult, surplusResults, maxResults, totalResults);
    }
    
    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalPages(int maxResults, int totalResults) {
        int totalPages = new Integer(totalResults / maxResults);
        if ((totalResults % maxResults) > 0)
            totalPages++;
        return totalPages;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getSurplusResults() {
        return this.surplusResults;
    }

    public void setSurplusResults(int surplusResults) {
        this.surplusResults = surplusResults;
    }
}
