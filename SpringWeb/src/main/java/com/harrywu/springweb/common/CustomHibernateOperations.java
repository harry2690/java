package com.harrywu.springweb.common;

import java.util.List;

import org.springframework.dao.DataAccessException;

@SuppressWarnings("rawtypes")
public interface CustomHibernateOperations {

    List find(final String queryString, final Object[] values,
              final int firstResult, final int maxResults) throws DataAccessException;
        
    List findByNamedParam(final String queryString, final String[] paramNames, final Object[] values,
                          final int firstResult, final int maxResults) throws DataAccessException;

    List findByValueBean(final String queryString, final Object valueBean,
                         final int firstResult, final int maxResults) throws DataAccessException;

    List findByNamedQuery(final String queryName, final Object[] values,
                          final int firstResult, final int maxResults) throws DataAccessException;

    List findByNamedQueryAndNamedParam(final String queryName, final String[] paramNames, final Object[] values,
                                       final int firstResult, final int maxResults) throws DataAccessException;

    List findByNamedQueryAndValueBean(final String queryName, final Object valueBean,
                                      final int firstResult, final int maxResults) throws DataAccessException;
}
