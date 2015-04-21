package com.harrywu.springweb.dao.impl;

import com.harrywu.springweb.dao.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by sadupa on 8/6/14.
 */
public class HibernateSessionFactoryImpl implements HibernateSessionFactory {

    private SessionFactory sessionFactory;

    @Override
    public Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
}
