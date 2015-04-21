package com.harrywu.springweb.common;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateStatistics extends HibernateDaoSupport {
    
//    private static Log log = LogFactory.getLog(HibernateStatistics.class);
    
    public void process() {
        getSessionFactory().getStatistics().logSummary();
    }
}
